package com.ks.musicdownloader.bandcamp;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.SongsDownloader;

public class BandcampDownloader extends SongsDownloader {

    public BandcampDownloader(ArtistInfo artistInfo, DownloadCallback downloadCallback) {
        super(artistInfo, downloadCallback);
    }

    @Override
    public void downloadSongs() {

    }
}
