package com.ks.musicdownloader.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.service.DownloaderService;
import com.ks.musicdownloader.service.ParserService;
import com.ks.musicdownloader.songsprocessors.MusicSite;

@SuppressWarnings("DanglingJavadoc")
public class DisplayListActivity extends AppCompatActivity {

    private static final String TAG = DisplayListActivity.class.getSimpleName();

    private String url;
    private MusicSite musicSite;
    private boolean networkConnected = false;
    private ParserService parserService;
    private DownloaderService downloaderService;
    private ServiceConnection parserServiceConnection;
    private ServiceConnection downloaderServiceConnection;
    private ConnectivityManager.NetworkCallback networkCallback;
    private DownloadCallback<ArtistInfo> downloadCallbackForParser;
    private DownloadCallback<ArtistInfo> downloadCallbackForDownloader;
    private ArtistInfo parsedArtistInfo;

    /**
     * Called when download button is clicked
     *
     * @param view view
     */
    public void downloadSongs(View view) {
        parsedArtistInfo = getSelectedSongs();
        bindToDownloaderService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getIntentExtras();
        performInitialSetup();
        bindToParserService();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterAllCallbacks();
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void getIntentExtras() {
        Intent intent = getIntent();
        url = intent.getStringExtra(Constants.DOWNLOAD_URL);
        String siteName = intent.getStringExtra(Constants.SITE_NAME);
        musicSite = Enum.valueOf(MusicSite.class, siteName);
    }

    private void performInitialSetup() {
        createNetworkCallback();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
        createServiceConnForParserService();
        createServiceConnForDownloaderService();
        createDownloadCallbackForParser();
        createDownloadCallbackForDownloader();
    }

    private void unRegisterAllCallbacks() {
        NetworkUtils.unRegReceiverForConnectionValidationOnly(this, networkCallback);
        if (parserService != null) {
            parserService.setDownloadCallback(null);
        }
        if (downloaderService != null) {
            downloaderService.setDownloadCallback(null);
        }
        stopService(new Intent(DisplayListActivity.this, ParserService.class));
        stopService(new Intent(DisplayListActivity.this, DownloaderService.class));
    }

    private void bindToParserService() {
        Intent intent = new Intent(DisplayListActivity.this, ParserService.class);
        bindService(intent, parserServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void notifyNothingToDownload() {
        // TODO: 04-09-2018 notify nothing to download and stop the activity 
    }

    private void displaySongsList() {
        Log.d(TAG, "displaySongsList");
        // TODO: 30-08-2018 display the parsedArtistInfo here
    }

    private ArtistInfo getSelectedSongs() {
        return new ArtistInfo();
    }

    private void bindToDownloaderService() {
        Intent intent = new Intent(DisplayListActivity.this, DownloaderService.class);
        bindService(intent, downloaderServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void createNetworkCallback() {
        Log.d(TAG, "createNetworkCallback()");
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                networkConnected = true;
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                networkConnected = false;
            }
        };
    }

    private void createServiceConnForParserService() {
        Log.d(TAG, "createServiceConnForParserService()");
        parserServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(TAG, "createServiceConnForParserService(): onServiceConnected start.");
                ParserService.LocalBinder binder = (ParserService.LocalBinder) iBinder;
                parserService = binder.getService();
                parserService.setDownloadCallback(downloadCallbackForParser);
                parserService.setMusicSite(musicSite);
                parserService.startDownload(url);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    private void createServiceConnForDownloaderService() {
        Log.d(TAG, "createServiceConnForDownloaderService()");
        downloaderServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                DownloaderService.LocalBinder binder = (DownloaderService.LocalBinder) iBinder;
                downloaderService = binder.getService();
                downloaderService.setDownloadCallback(downloadCallbackForDownloader);
                downloaderService.startDownload(parsedArtistInfo);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    private void createDownloadCallbackForParser() {
        Log.d(TAG, "createDownloadCallbackForParser()");
        downloadCallbackForParser = new DownloadCallback<ArtistInfo>() {
            @Override
            public void updateFromDownload(ArtistInfo result) {
                parsedArtistInfo = result;
            }

            @Override
            public void onProgressUpdate(int progressCode, int percentComplete) {
                switch (progressCode) {
                    case Progress.ERROR:
                        break;
                    case Progress.CONNECT_SUCCESS:
                        break;
                    case Progress.GET_INPUT_STREAM_SUCCESS:
                        break;
                    case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                        break;
                    case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                        break;
                }
            }

            @Override
            public void finishDownloading() {
                if (parsedArtistInfo == Constants.DUMMY_ARTIST_INFO) {
                    notifyNothingToDownload();
                } else {
                    displaySongsList();
                }
            }
        };
    }

    private void createDownloadCallbackForDownloader() {
        Log.d(TAG, "createDownloadCallbackForDownloader()");
        downloadCallbackForDownloader = new DownloadCallback<ArtistInfo>() {
            @Override
            public void updateFromDownload(ArtistInfo artistInfo) {

            }

            @Override
            public void onProgressUpdate(int progressCode, int percentComplete) {
                switch (progressCode) {
                    case Progress.ERROR:
                        break;
                    case Progress.CONNECT_SUCCESS:
                        break;
                    case Progress.GET_INPUT_STREAM_SUCCESS:
                        break;
                    case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                        break;
                    case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                        break;
                }
            }

            @Override
            public void finishDownloading() {
            }
        };
    }
}
