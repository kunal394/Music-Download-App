package com.ks.musicdownloader.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.service.SongsDownloadService;
import com.ks.musicdownloader.service.SongsListDownloadService;

public class DisplayMessageActivity extends FragmentActivity {

    private static final String TAG = DisplayMessageActivity.class.getSimpleName();
    private boolean networkConnected = true;
    private SongsDownloadService songsDownloadService;
    private SongsListDownloadService songsListDownloadService;
    private ServiceConnection songsListDownloadServiceConnection;
    private ServiceConnection songsDownloadServiceConnection;
    private ConnectivityManager.NetworkCallback networkCallback;
    private DownloadCallback<ArtistInfo> downloadCallbackForSongsListDownload;
    private DownloadCallback<ArtistInfo> downloadCallbackForSongsDownload;
    private ArtistInfo parsedArtistInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        performInitialSetup();

        String url = savedInstanceState.getString(MainActivity.DOWNLOAD_URL);
        fetchSongsList(url);
        displaySongsList(parsedArtistInfo);
        downloadSelectedSongs(getSelectedSongs());
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

    private void unRegisterAllCallbacks() {
        NetworkUtils.unRegReceiverForConnectionValidationOnly(this, networkCallback);
        songsListDownloadService.setDownloadCallback(null);
        songsDownloadService.setDownloadCallback(null);
        Intent songsListDownloadServiceintent = new Intent(DisplayMessageActivity.this, SongsListDownloadService.class);
        Intent songsDownloadServiceintent = new Intent(DisplayMessageActivity.this, SongsListDownloadService.class);
        stopService(songsListDownloadServiceintent);
        stopService(songsDownloadServiceintent);
    }

    private void performInitialSetup() {
        createNetworkCallback();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
        createServiceConnForSongsListDownloadService();
        createServiceConnForSongsDownloadService();
        regDownloadCallbackForSongsList();
        regDownloadCallbackForSongs();
    }

    private void fetchSongsList(String url) {
        Intent intent = new Intent(DisplayMessageActivity.this, SongsListDownloadService.class);
        bindService(intent, songsListDownloadServiceConnection, Context.BIND_AUTO_CREATE);
        songsListDownloadService.startDownload(url);
    }

    private void displaySongsList(ArtistInfo artistInfo) {

    }

    private ArtistInfo getSelectedSongs() {
        return new ArtistInfo();
    }

    private void downloadSelectedSongs(ArtistInfo artistInfo) {
        Intent intent = new Intent(DisplayMessageActivity.this, SongsListDownloadService.class);
        bindService(intent, songsDownloadServiceConnection, Context.BIND_AUTO_CREATE);
        songsDownloadService.startDownload(artistInfo);
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

    private void createServiceConnForSongsListDownloadService() {
        Log.d(TAG, "createServiceConnForSongsListDownloadService()");
        songsListDownloadServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SongsListDownloadService.LocalBinder binder = (SongsListDownloadService.LocalBinder) iBinder;
                songsListDownloadService = binder.getService();
                songsListDownloadService.setDownloadCallback(downloadCallbackForSongsListDownload);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    private void createServiceConnForSongsDownloadService() {
        Log.d(TAG, "createServiceConnForSongsDownloadService()");
        songsDownloadServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SongsDownloadService.LocalBinder binder = (SongsDownloadService.LocalBinder) iBinder;
                songsDownloadService = binder.getService();
                songsDownloadService.setDownloadCallback(downloadCallbackForSongsDownload);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    private void regDownloadCallbackForSongsList() {
        Log.d(TAG, "regDownloadCallbackForSongsList()");
        downloadCallbackForSongsListDownload = new DownloadCallback<ArtistInfo>() {
            @Override
            public void updateFromDownload(ArtistInfo result) {

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

    private void regDownloadCallbackForSongs() {
        Log.d(TAG, "regDownloadCallbackForSongs()");
        downloadCallbackForSongsDownload = new DownloadCallback<ArtistInfo>() {
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
