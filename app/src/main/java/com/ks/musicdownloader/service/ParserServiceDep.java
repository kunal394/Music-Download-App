package com.ks.musicdownloader.service;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.songsprocessors.MusicSite;

import java.lang.ref.WeakReference;

public class ParserServiceDep extends BaseDownloadService<String, ArtistInfo> {

    private static final String TAG = ParserServiceDep.class.getSimpleName();
    private final IBinder binder = new LocalBinder();
    private MusicSite musicSite;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public ParserServiceDep getService() {
            return ParserServiceDep.this;
        }
    }

    public void startDownload(String url) {
        new FetchSongsListTask(this).execute(url);
    }

    public void onTaskCompletion() {
        stopSelf();
    }

    public MusicSite getMusicSite() {
        return musicSite;
    }

    public void setMusicSite(MusicSite musicSite) {
        this.musicSite = musicSite;
    }

    private static class FetchSongsListTask extends AsyncTask<String, String, ArtistInfo> {

        // making this class static to avoid memory leak, since now static inner class can not access the members of its outer class
        // and keeping a weak reference to the service in order to use the service methods and variables from inside this class
        private final WeakReference<ParserServiceDep> service;

        FetchSongsListTask(ParserServiceDep service) {
            this.service = new WeakReference<>(service);
        }

        @Override
        protected ArtistInfo doInBackground(String... strings) {
            String url = strings[0];
            DownloadCallback downloadCallback;
            MusicSite musicSite;
            ArtistInfo artistInfo = Constants.DUMMY_ARTIST_INFO;

            ParserServiceDep parserServiceDep = service.get();
            if (parserServiceDep == null) {
                Log.d(TAG, "FetchSongsListTask doInBackground(): Service found null!");
                return artistInfo;
            } else {
                downloadCallback = parserServiceDep.getDownloadCallback();
                musicSite = parserServiceDep.getMusicSite();
            }

            if (downloadCallback == null || musicSite == null) {
                Log.d(TAG, "FetchSongsListTask doInBackground(): Null MusicSite: " + musicSite + " or download callback: " + downloadCallback);
                return artistInfo;
            }

            try {
                artistInfo = musicSite.getMusicParserDep(url, downloadCallback).parseArtistInfo();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "FetchSongsListTask doInBackground(): Error while parsing the songs info! Error: " + e);
            }
            return artistInfo;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ParserServiceDep parserServiceDep = service.get();
            if (parserServiceDep == null) {
                Log.d(TAG, "FetchSongsListTask onProgressUpdate(): Service found null!");
                return;
            }
            parserServiceDep.getDownloadCallback().updateFromDownload(null);
        }

        @Override
        protected void onPostExecute(ArtistInfo artistInfo) {
            super.onPostExecute(artistInfo);
            ParserServiceDep parserServiceDep = service.get();
            if (parserServiceDep == null) {
                Log.d(TAG, "FetchSongsListTask onPostExecute(): Service found null!");
                return;
            }
            DownloadCallback<ArtistInfo> downloadCallback = parserServiceDep.getDownloadCallback();
            if (downloadCallback != null) {
                downloadCallback.updateFromDownload(artistInfo);
                downloadCallback.finishDownloading();
            }
            parserServiceDep.onTaskCompletion();
        }
    }
}
