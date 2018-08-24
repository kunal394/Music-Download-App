package com.ks.musicdownloader;

import java.io.IOException;

public abstract class SongsParser {

    private String url;

    private DownloadCallback downloadCallback;

    public SongsParser(String url, DownloadCallback downloadCallback) {
        this.url = url;
        this.downloadCallback = downloadCallback;
    }

    public abstract ArtistInfo parseArtistInfo() throws IOException;

    protected String getUrl() {
        return url;
    }

    protected DownloadCallback getDownloadCallback() {
        return downloadCallback;
    }
}
