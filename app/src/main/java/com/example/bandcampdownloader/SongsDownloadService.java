package com.example.bandcampdownloader;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.lang.ref.WeakReference;

public class SongsDownloadService extends DownloadService<ArtistInfo, ArtistInfo> {

    private static final String TAG = SongsDownloadService.class.getSimpleName();
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
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
        new SongsDownloadTask(this).execute(artistInfo);
    }

    public class LocalBinder extends Binder {
        SongsDownloadService getService() {
            return SongsDownloadService.this;
        }
    }

    private static class SongsDownloadTask extends AsyncTask<ArtistInfo, String, ArtistInfo> {

        // making this class static t avoid memory leak, since now static inner class can not access the members of its outer class
        // and keeping a weak reference to the service in order to use the service methods and variables from inside this class
        private final WeakReference<SongsDownloadService> service;

        SongsDownloadTask(SongsDownloadService service) {
            this.service = new WeakReference<>(service);
        }

        @Override
        protected ArtistInfo doInBackground(ArtistInfo... artistInfos) {
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            SongsDownloadService songsDownloadService = service.get();
            if (songsDownloadService == null) {
                Log.d(TAG, "SongsDownloadTask onProgressUpdate(): Service found null!");
                return ;
            }
            songsDownloadService.getDownloadCallback().updateFromDownload(null);
        }

        @Override
        protected void onPostExecute(ArtistInfo artistInfo) {
            super.onPostExecute(artistInfo);
            SongsDownloadService songsDownloadService = service.get();
            if (songsDownloadService == null) {
                Log.d(TAG, "SongsDownloadTask onPostExecute(): Service found null!");
                return;
            }
            songsDownloadService.getDownloadCallback().finishDownloading();
            songsDownloadService.onTaskCompletion();
        }
    }
}
