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
import com.ks.musicdownloader.service.DownloaderService;
import com.ks.musicdownloader.service.ParserService;
import com.ks.musicdownloader.songsprocessors.SongsFactory;
import com.ks.musicdownloader.songsprocessors.SongsProcessors;

public class DisplayMessageActivity extends FragmentActivity {

    private static final String TAG = DisplayMessageActivity.class.getSimpleName();

    private String url;
    private boolean networkConnected = true;
    private ParserService parserService;
    private DownloaderService downloaderService;
    private ServiceConnection parserServiceConnection;
    private ServiceConnection downloaderServiceConnection;
    private ConnectivityManager.NetworkCallback networkCallback;
    private DownloadCallback<ArtistInfo> downloadCallbackForParser;
    private DownloadCallback<ArtistInfo> downloadCallbackForDownloader;
    private ArtistInfo parsedArtistInfo;
    private SongsProcessors songsProcessors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        url = savedInstanceState.getString(MainActivity.DOWNLOAD_URL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        performInitialSetup();
        songsProcessors = SongsFactory.getInstance().
                getSongsProcessors(url, downloadCallbackForParser, downloadCallbackForDownloader);
        if (songsProcessors == null) {
            Log.d(TAG, "onStart(): No suitable processor found for the given url: " + url);
        }
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
        parserService.setDownloadCallback(null);
        downloaderService.setDownloadCallback(null);
        stopService(new Intent(DisplayMessageActivity.this, ParserService.class));
        stopService(new Intent(DisplayMessageActivity.this, DownloaderService.class));
    }

    private void performInitialSetup() {
        createNetworkCallback();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
        createServiceConnForParserService();
        createServiceConnForDownloaderService();
        createDownloadCallbackForParser();
        createDownloadCallbackForDownloader();
    }

    private void fetchSongsList(String url) {
        Intent intent = new Intent(DisplayMessageActivity.this, ParserService.class);
        bindService(intent, parserServiceConnection, Context.BIND_AUTO_CREATE);
        parserService.startDownload(url);
    }

    private void displaySongsList(ArtistInfo artistInfo) {

    }

    private ArtistInfo getSelectedSongs() {
        return new ArtistInfo();
    }

    private void downloadSelectedSongs(ArtistInfo artistInfo) {
        Intent intent = new Intent(DisplayMessageActivity.this, ParserService.class);
        bindService(intent, downloaderServiceConnection, Context.BIND_AUTO_CREATE);
        downloaderService.startDownload(artistInfo);
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
                ParserService.LocalBinder binder = (ParserService.LocalBinder) iBinder;
                parserService = binder.getService();
                parserService.setDownloadCallback(downloadCallbackForParser);
                parserService.setSongsParser(songsProcessors.getSongsParser());
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
                downloaderService.setSongsDownloader(songsProcessors.getSongsDownloader());
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
