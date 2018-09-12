package com.ks.musicdownloader.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.SongInfo;
import com.ks.musicdownloader.songsprocessors.MusicSite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DownloaderService extends BaseDownloadService<ArtistInfo, Integer> {

    private static final String TAG = DownloaderService.class.getSimpleName();

    private final IBinder binder = new LocalBinder();
    private MusicSite musicSite;
    private ArtistInfo parsedArtistInfo;
    private SparseArray<Long> songsDownloadReferences;
    private DownloadManager dm;

    HandlerThread handlerThread = new HandlerThread(Constants.DOWNLOAD_THREAD);
    Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        handlerThread.start();
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onTaskCompletion() {
        stopSelf();
    }

    @Override
    public void startDownload(ArtistInfo artistInfo) {
        Log.d(TAG, "startDownload()");
        dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        parsedArtistInfo = artistInfo;
        createHandler();
        handler.sendEmptyMessage(Constants.ENQUEUE_SONGS);
    }

    public void setMusicSite(MusicSite musicSite) {
        this.musicSite = musicSite;
    }

    public class LocalBinder extends Binder {
        public DownloaderService getService() {
            return DownloaderService.this;
        }
    }

    private void createHandler() {
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage()");
                super.handleMessage(msg);
                switch (msg.what) {
                    case Constants.ENQUEUE_SONGS:
                        Log.d(TAG, "handleMessage() enqueue songs");
                        enqueueSongsForDownload();
                        break;
                }
            }
        };
    }

    private void enqueueSongsForDownload() {
        Log.d(TAG, "enqueueSongsForDownload()");
        songsDownloadReferences = new SparseArray<>();
        if (dm == null) {
            Log.d(TAG, "startDownload(): dm found null!");
            return;
        }
        HashMap<String, List<Integer>> albumInfo = parsedArtistInfo.getAlbumInfo();
        SparseArray<SongInfo> songsMap = parsedArtistInfo.getSongsMap();
        for (Map.Entry<String, List<Integer>> albumEntry : albumInfo.entrySet()) {
            String album = albumEntry.getKey();
            List<Integer> songIds = albumEntry.getValue();
            for (Integer songId : songIds) {
                SongInfo songInfo = songsMap.get(songId);
                Log.d(TAG, "enqueueSongsForDownload() adding song: " + songInfo.getName() + " for download.");
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(songInfo.getUrl()));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, musicSite.createFilePath(parsedArtistInfo.getArtist(), album, songInfo.getName()));
                songsDownloadReferences.put(songId, dm.enqueue(request));
            }
        }
    }
}
