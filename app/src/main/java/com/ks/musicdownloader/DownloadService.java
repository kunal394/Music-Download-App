package com.ks.musicdownloader;

import android.app.Service;

public abstract class DownloadService<Input, Res> extends Service {

    protected DownloadCallback<Res> downloadCallback;

    public void setDownloadCallback(DownloadCallback<Res> downloadCallback) {
        this.downloadCallback = downloadCallback;
    }

    public DownloadCallback<Res> getDownloadCallback() {
        return downloadCallback;
    }

    public abstract void onTaskCompletion();

    public abstract void startDownload(Input input);
}
