package com.example.bandcampdownloader.bandcamp;

import com.example.bandcampdownloader.ArtistInfo;
import com.example.bandcampdownloader.DownloadCallback;
import com.example.bandcampdownloader.SongsInfoParser;

public class BandcampParser extends SongsInfoParser{

    public BandcampParser(String url, DownloadCallback downloadCallback) {
        super(url, downloadCallback);
    }

    @Override
    public ArtistInfo parseArtistInfo() {
        return new ArtistInfo();
    }
}
