package com.example.bandcampdownloader;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class SongsListDownloadService extends Service {

    private final IBinder binder = new LocalBinder();

    private DownloadCallback downloadCallback;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public ArtistInfo fetchSongsList(String url) {
        return new ArtistInfo();
    }

    public class LocalBinder extends Binder {
        SongsListDownloadService getService() {
            return SongsListDownloadService.this;
        }
    }

    public void startDownload(ArtistInfo artistInfo) {
    }

    public void setDownloadCallback(DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }

    public DownloadCallback getDownloadCallback() {
        return downloadCallback;
    }

    private static class FetchSongsListTask extends AsyncTask<String, String, ArtistInfo> {

        // making this class static t avoid memory leak, since now static inner class can not access the members of its outer class
        // and keeping a weak reference to the service in order to use the service methods and variables from inside this class
        private final WeakReference<SongsListDownloadService> service;

        public FetchSongsListTask(SongsListDownloadService service) {
            this.service = new WeakReference<>(service);
        }

        @Override
        protected ArtistInfo doInBackground(String... strings) {
            publishProgress("");
            return null;
        }

        @Override
        protected void onPostExecute(ArtistInfo artistInfo) {
            super.onPostExecute(artistInfo);
            service.get().getDownloadCallback().finishDownloading();
        }
    }
}
