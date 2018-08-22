package com.example.bandcampdownloader;

public abstract class SongsInfoParser {

    private String url;

    private DownloadCallback downloadCallback;

    public SongsInfoParser(String url, DownloadCallback downloadCallback) {
        this.url = url;
        this.downloadCallback = downloadCallback;
    }

    public abstract ArtistInfo parseArtistInfo();
}
