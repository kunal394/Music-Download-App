package com.ks.musicdownloader.service;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.songsprocessors.SongsParser;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampParser;

import java.lang.ref.WeakReference;

public class ParserService extends BaseDownloadService<String, ArtistInfo> {

    private static final String TAG = ParserService.class.getSimpleName();
    private final IBinder binder = new LocalBinder();
    private SongsParser songsParser;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public ParserService getService() {
            return ParserService.this;
        }
    }

    public void startDownload(String url) {
        new FetchSongsListTask(this).execute(url);
    }

    public void onTaskCompletion() {
        stopSelf();
    }

    public void setSongsParser(SongsParser songsParser) {
        this.songsParser = songsParser;
    }

    public SongsParser getSongsParser() {
        return songsParser;
    }

    private static class FetchSongsListTask extends AsyncTask<String, String, ArtistInfo> {

        // making this class static to avoid memory leak, since now static inner class can not access the members of its outer class
        // and keeping a weak reference to the service in order to use the service methods and variables from inside this class
        private final WeakReference<ParserService> service;

        FetchSongsListTask(ParserService service) {
            this.service = new WeakReference<>(service);
        }

        @Override
        protected ArtistInfo doInBackground(String... strings) {
            ParserService parserService = service.get();
            DownloadCallback downloadCallback = null;
            if (parserService == null) {
                Log.d(TAG, "FetchSongsListTask doInBackground(): Service found null!");
            } else {
                downloadCallback = parserService.getDownloadCallback();
            }
            ArtistInfo artistInfo = null;
            try {
                artistInfo = new BandcampParser(strings[0], downloadCallback).parseArtistInfo();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Error while parsing the songs info! Error: " + e);
            }
            return artistInfo;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ParserService parserService = service.get();
            if (parserService == null) {
                Log.d(TAG, "FetchSongsListTask onProgressUpdate(): Service found null!");
                return;
            }
            parserService.getDownloadCallback().updateFromDownload(null);
        }

        @Override
        protected void onPostExecute(ArtistInfo artistInfo) {
            super.onPostExecute(artistInfo);
            ParserService parserService = service.get();
            if (parserService == null) {
                Log.d(TAG, "FetchSongsListTask onPostExecute(): Service found null!");
                return;
            }
            parserService.getDownloadCallback().updateFromDownload(artistInfo);
            parserService.getDownloadCallback().finishDownloading();
            parserService.onTaskCompletion();
        }
    }
}
