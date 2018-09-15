package com.ks.musicdownloader.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.SongInfo;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.service.DownloaderService;
import com.ks.musicdownloader.service.ParserServiceDep;
import com.ks.musicdownloader.songsprocessors.MusicSite;

@SuppressWarnings("DanglingJavadoc")
public class DisplayListActivity extends AppCompatActivity {

    private static final String TAG = DisplayListActivity.class.getSimpleName();

    private String url;
    private MusicSite musicSite;
    private boolean networkConnected = false;
    private ParserServiceDep parserServiceDep;
    private DownloaderService downloaderService;
    private ServiceConnection parserServiceConnection;
    private ServiceConnection downloaderServiceConnection;
    private boolean parserServiceBound = false;
    private boolean downloaderServiceBound = false;
    private ConnectivityManager.NetworkCallback networkCallback;
    private DownloadCallback<ArtistInfo> downloadCallbackForParser;
    private DownloadCallback<Integer> downloadCallbackForDownloader;
    private ArtistInfo parsedArtistInfo;
    private RelativeLayout progressBarLayout;
    private TextView nothingToDownloadView;
    private FloatingActionButton downloadButton;

    /**
     * Called when download button is clicked
     *
     * @param view view
     */
    public void downloadSongs(View view) {
        downloadButton.hide();
        parsedArtistInfo = getSelectedSongs();
        bindToDownloaderService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);
        progressBarLayout = findViewById(R.id.parserProgressBarLayout);
        progressBarLayout.setVisibility(View.VISIBLE);
        nothingToDownloadView = findViewById(R.id.nothingToDownloadView);
        downloadButton = findViewById(R.id.download_button);
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
    protected void onResume() {
        super.onResume();
        // TODO: 14-09-2018 displays the download button again if press home and then come back, sometimes!!!
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterAllCallbacks();
        // TODO: 14-09-2018 gives service not registered error, check why???
        if (parserServiceBound) {
            unbindService(parserServiceConnection);
        }
        if (downloaderServiceBound) {
            unbindService(downloaderServiceConnection);
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void getIntentExtras() {
        Intent intent = getIntent();
        url = intent.getStringExtra(Constants.DOWNLOAD_URL);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
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
        if (parserServiceDep != null) {
            parserServiceDep.setDownloadCallback(null);
        }
        if (downloaderService != null) {
            downloaderService.setDownloadCallback(null);
        }
//        stopService(new Intent(DisplayListActivity.this, ParserServiceDep.class));
//        stopService(new Intent(DisplayListActivity.this, DownloaderService.class));
    }

    private void bindToParserService() {
        Intent intent = new Intent(DisplayListActivity.this, ParserServiceDep.class);
        bindService(intent, parserServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void notifyNothingToDownload() {
        Log.d(TAG, "notifyNothingToDownload()");
        hideProgressBar();
        nothingToDownloadView.setVisibility(View.VISIBLE);
    }

    private void notifyDownloadedSong(Integer downloadedSongId) {
        // TODO: 12-09-2018 complete this
        SongInfo downloadedSongInfo = parsedArtistInfo.getSongsMap().get(downloadedSongId);
    }

    @SuppressLint("RestrictedApi")
    private void displaySongsList() {
        Log.d(TAG, "displaySongsList()");
        hideProgressBar();
        downloadButton.show();
        downloadButton.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBarLayout.setVisibility(View.GONE);
    }

    private ArtistInfo getSelectedSongs() {
        return parsedArtistInfo;
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
                ParserServiceDep.LocalBinder binder = (ParserServiceDep.LocalBinder) iBinder;
                parserServiceDep = binder.getService();
                parserServiceDep.setDownloadCallback(downloadCallbackForParser);
                parserServiceDep.setMusicSite(musicSite);
                parserServiceBound = true;
                parserServiceDep.startDownload(url);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG, "createServiceConnForParserService()");
                parserServiceBound = false;
            }
        };
    }

    private void createServiceConnForDownloaderService() {
        Log.d(TAG, "createServiceConnForDownloaderService()");
        downloaderServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(TAG, "createServiceConnForDownloaderService() onServiceConnected");
                DownloaderService.LocalBinder binder = (DownloaderService.LocalBinder) iBinder;
                downloaderService = binder.getService();
                downloaderService.setDownloadCallback(downloadCallbackForDownloader);
                downloaderService.setMusicSite(musicSite);
                downloaderServiceBound = true;
                downloaderService.startDownload(parsedArtistInfo);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG, "createServiceConnForDownloaderService() onServiceDisconnected");
                downloaderServiceBound = false;
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
                if (parserServiceBound) {
                    unbindService(parserServiceConnection);
                    parserServiceBound = false;
                }
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
        downloadCallbackForDownloader = new DownloadCallback<Integer>() {
            @Override
            public void updateFromDownload(Integer downloadedSongId) {
                notifyDownloadedSong(downloadedSongId);
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
                if (downloaderServiceBound) {
                    unbindService(downloaderServiceConnection);
                    downloaderServiceBound = false;
                }
            }
        };
    }
}
