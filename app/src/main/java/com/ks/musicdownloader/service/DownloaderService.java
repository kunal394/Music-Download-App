package com.ks.musicdownloader.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.ks.musicdownloader.Utils.FileUtils;
import com.ks.musicdownloader.Utils.PrefUtils;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.common.SongInfo;
import com.ks.musicdownloader.songsprocessors.MusicSite;

/**
 * Created by Kunal Singh(knl.singh) on 16-10-2018.
 */
@SuppressWarnings("DanglingJavadoc")
public class DownloaderService extends IntentService {

    private static final String TAG = DownloaderService.class.getSimpleName();

    private MusicSite musicSite;
    private ArtistInfo parsedArtistInfo;
    private LongSparseArray<Integer> songsDownloadReferences;
    private DownloadManager dm;
    private BroadcastReceiver onCompleteBroadcastReceiver;

    HandlerThread handlerThread = new HandlerThread(Constants.DOWNLOAD_THREAD);
    Handler handler;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloaderService(String name) {
        super(name);
    }

    public DownloaderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        if (intent == null) {
            Log.d(TAG, "onHandleIntent(): Intent null!");
            return;
        }
        getIntentExtras(intent);
        init();
        // TODO: 16-10-2018 handle the download updates
        handler.sendEmptyMessage(Constants.ENQUEUE_SONGS);
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void init() {
        createBroadcastReceiverForCompletedDownloads();
        registerReceiver(onCompleteBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        createHandler();
    }

    private void getIntentExtras(Intent intent) {
        Log.d(TAG, "getIntentExtras(): ");
        parsedArtistInfo = intent.getParcelableExtra(Constants.PARSED_ARTIST_INFO);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
        Log.d(TAG, "getIntentExtras() sitename: " + siteName);
        musicSite = Enum.valueOf(MusicSite.class, siteName);
        Log.d(TAG, "getIntentExtras() artInfo: " + parsedArtistInfo.toString());
    }

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
            String filePath = musicSite.createFilePath(PrefUtils.putPrefStringIfNull(getApplicationContext(),
                    Constants.SETTINGS_PREF_NAME, Constants.PREF_DEFAULT_SONGS_FOLDER_KEY, Constants.MUSIC_DIRECTORY)
                    , songInfo.getAlbum(), songInfo.getName(), parsedArtistInfo.getArtist());
            if (FileUtils.doesFileExists(filePath)) {
                Log.d(TAG, "file: " + filePath + " already exists. So not downloading again.");
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
