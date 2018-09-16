package com.ks.musicdownloader.activity;

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
import android.widget.TextView;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.SongInfo;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.service.DownloaderService;
import com.ks.musicdownloader.songsprocessors.MusicSite;

@SuppressWarnings("DanglingJavadoc")
public class DisplayListActivity extends AppCompatActivity {

    private static final String TAG = DisplayListActivity.class.getSimpleName();

    private MusicSite musicSite;
    private boolean networkConnected = false;
    private DownloaderService downloaderService;
    private ServiceConnection downloaderServiceConnection;
    private ConnectivityManager.NetworkCallback networkCallback;
    private DownloadCallback<Integer> downloadCallbackForDownloader;
    private ArtistInfo parsedArtistInfo;
    private TextView artistView;
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
        downloadButton = findViewById(R.id.download_button);
        artistView = findViewById(R.id.artistView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getIntentExtras();
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        parsedArtistInfo = intent.getParcelableExtra(Constants.PARSED_ARTIST_INFO);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
        artistView.setText(parsedArtistInfo.getArtist());
        musicSite = Enum.valueOf(MusicSite.class, siteName);
    }

    private void init() {
        createNetworkCallback();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
        createServiceConnForDownloaderService();
        createDownloadCallbackForDownloader();
    }

    private void unRegisterAllCallbacks() {
        NetworkUtils.unRegReceiverForConnectionValidationOnly(this, networkCallback);
        if (downloaderService != null) {
            downloaderService.setDownloadCallback(null);
        }
    }

    private void notifyDownloadedSong(Integer downloadedSongId) {
        // TODO: 12-09-2018 complete this
        SongInfo downloadedSongInfo = parsedArtistInfo.getSongsMap().get(downloadedSongId);
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
                downloaderService.startDownload(parsedArtistInfo);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG, "createServiceConnForDownloaderService() onServiceDisconnected");
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
            }
        };
    }
}
