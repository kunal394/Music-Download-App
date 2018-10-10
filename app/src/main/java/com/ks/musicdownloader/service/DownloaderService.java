package com.ks.musicdownloader.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.Utils.FileUtils;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.common.SongInfo;
import com.ks.musicdownloader.songsprocessors.MusicSite;


@SuppressWarnings("DanglingJavadoc")
public class DownloaderService extends BaseDownloadService<ArtistInfo, Integer> {

    private static final String TAG = DownloaderService.class.getSimpleName();

    private final IBinder binder = new LocalBinder();
    private MusicSite musicSite;
    private ArtistInfo parsedArtistInfo;
    private LongSparseArray<Integer> songsDownloadReferences;
    private DownloadManager dm;
    private BroadcastReceiver onCompleteBroadcastReceiver;

    HandlerThread handlerThread = new HandlerThread(Constants.DOWNLOAD_THREAD);
    Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        handlerThread.start();
        createBroadcastReceiverForCompletedDownloads();
        registerReceiver(onCompleteBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onTaskCompletion() {
        unregisterReceiver(onCompleteBroadcastReceiver);
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

    /******************Private************************************/
    /******************Methods************************************/

    private void createBroadcastReceiverForCompletedDownloads() {
        onCompleteBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadedReferenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.d(TAG, "onCompleteBroadcastReceiver onReceive() download id: " + downloadedReferenceId);
                if (downloadedReferenceId == -1) {
                    return;
                }
                Integer downloadedSongId = songsDownloadReferences.get(downloadedReferenceId);
                downloadCallback.updateFromDownload(downloadedSongId);
            }
        };
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
        songsDownloadReferences = new LongSparseArray<>();
        if (dm == null) {
            Log.d(TAG, "startDownload(): dm found null!");
            return;
        }
        SparseArray<SongInfo> songsMap = parsedArtistInfo.getSongsMap();
        for (int i = 0; i < songsMap.size(); i++) {
            SongInfo songInfo = songsMap.valueAt(i);
            if (!songInfo.isChecked()) {
                Log.d(TAG, "Song: " + songInfo.getName() + " of album: " + songInfo.getAlbum()
                        + " of artist: " + parsedArtistInfo.getArtist() + " not marked for download.");
                continue;
            }
            String filePath = musicSite.createFilePath(CommonUtils.putPrefStringIfNull(getApplicationContext(),
                    Constants.SETTINGS_PREF_NAME, Constants.PREF_DEFAULT_SONGS_FOLDER_KEY, Constants.MUSIC_DIRECTORY)
                    , songInfo.getAlbum(), songInfo.getName(), parsedArtistInfo.getArtist());
            if (FileUtils.doesFileExists(filePath)) {
                Log.d(TAG, "file: " + filePath + " already exists. So not downloading again.");
                downloadCallback.updateFromDownload(songInfo.getId());
                continue;
            }
            Log.d(TAG, "enqueueSongsForDownload() adding song: " + songInfo.getName() + " for download.");
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(songInfo.getUrl()));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationUri(Uri.parse(filePath));
            request.setMimeType(Constants.AUDIO_MIME_TYPE);
            songsDownloadReferences.put(dm.enqueue(request), songInfo.getId());
        }
    }
}
