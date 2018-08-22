package com.example.bandcampdownloader;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class SongsDownloader {

    private static final String TAG = SongsDownloader.class.getSimpleName();

    private ArtistInfo artistInfo;

    private DownloadCallback downloadCallback;

    public SongsDownloader(ArtistInfo artistInfo, DownloadCallback downloadCallback) {
        this.artistInfo = artistInfo;
        this.downloadCallback = downloadCallback;
    }

    public abstract void downloadSongs();

    protected boolean downloadAndSaveSong(String url, String filePath) {
        try {
            URLConnection connection = new URL(url).openConnection();
            InputStream inputStream = connection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "downloadAndSaveSong(): error: " + e);
            return false;
        }
        return true;
    }
}
