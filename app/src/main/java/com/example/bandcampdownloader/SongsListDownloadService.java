package com.example.bandcampdownloader;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.bandcampdownloader.bandcamp.BandcampParser;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class SongsListDownloadService extends DownloadService<String, ArtistInfo> {

    private static final String TAG = SongsListDownloadService.class.getSimpleName();
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        SongsListDownloadService getService() {
            return SongsListDownloadService.this;
        }
    }

    public void startDownload(String url) {
        new FetchSongsListTask(this).execute(url);
    }

    public void onTaskCompletion() {
        stopSelf();
    }

    private static class FetchSongsListTask extends AsyncTask<String, String, ArtistInfo> {

        // making this class static t avoid memory leak, since now static inner class can not access the members of its outer class
        // and keeping a weak reference to the service in order to use the service methods and variables from inside this class
        private final WeakReference<SongsListDownloadService> service;

        FetchSongsListTask(SongsListDownloadService service) {
            this.service = new WeakReference<>(service);
        }

        @Override
        protected ArtistInfo doInBackground(String... strings) {
            SongsListDownloadService songsListDownloadService = service.get();
            DownloadCallback downloadCallback = null;
            if (songsListDownloadService == null) {
                Log.d(TAG, "FetchSongsListTask doInBackground(): Service found null!");
            } else {
                downloadCallback = songsListDownloadService.getDownloadCallback();
            }
            new BandcampParser(strings[0], downloadCallback);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            SongsListDownloadService songsListDownloadService = service.get();
            if (songsListDownloadService == null) {
                Log.d(TAG, "FetchSongsListTask onProgressUpdate(): Service found null!");
                return ;
            }
            songsListDownloadService.getDownloadCallback().updateFromDownload(null);
        }

        @Override
        protected void onPostExecute(ArtistInfo artistInfo) {
            super.onPostExecute(artistInfo);
            SongsListDownloadService songsListDownloadService = service.get();
            if (songsListDownloadService == null) {
                Log.d(TAG, "FetchSongsListTask onPostExecute(): Service found null!");
                return;
            }
            songsListDownloadService.getDownloadCallback().finishDownloading();
            songsListDownloadService.onTaskCompletion();
        }
    }
}
