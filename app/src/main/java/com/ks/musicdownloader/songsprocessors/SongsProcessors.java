package com.ks.musicdownloader.songsprocessors;

public class SongsProcessors {

    private SongsDownloader songsDownloader;

    private SongsParser songsParser;

    public SongsProcessors(SongsDownloader songsDownloader, SongsParser songsParser) {
        this.songsDownloader = songsDownloader;
        this.songsParser = songsParser;
    }

    public SongsProcessors() {

    }

    public SongsDownloader getSongsDownloader() {
        return songsDownloader;
    }

    public void setSongsDownloader(SongsDownloader songsDownloader) {
        this.songsDownloader = songsDownloader;
    }

    public SongsParser getSongsParser() {
        return songsParser;
    }

    public void setSongsParser(SongsParser songsParser) {
        this.songsParser = songsParser;
    }
}
