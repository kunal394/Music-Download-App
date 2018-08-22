package com.example.bandcampdownloader;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SongsDownloadService extends Service {

    private final IBinder binder = new LocalBinder();
    private DownloadCallback downloadCallback;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    public class LocalBinder extends Binder {
        SongsDownloadService getService() {
            return SongsDownloadService.this;
        }
    }

    public void setDownloadCallback(DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }
}
