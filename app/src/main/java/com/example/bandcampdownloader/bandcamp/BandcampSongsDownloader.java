package com.example.bandcampdownloader.bandcamp;

import com.example.bandcampdownloader.ArtistInfo;
import com.example.bandcampdownloader.DownloadCallback;
import com.example.bandcampdownloader.SongsDownloader;

public class BandcampSongsDownloader extends SongsDownloader {

    public BandcampSongsDownloader(ArtistInfo artistInfo, DownloadCallback downloadCallback) {
        super(artistInfo, downloadCallback);
    }

    @Override
    public void downloadSongs() {

    }
}
