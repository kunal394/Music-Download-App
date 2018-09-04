package com.ks.musicdownloader.songsprocessors;

public class SongsProcessors {

    private SongsDownloader songsDownloader;

    private BaseParser baseParser;

    public SongsProcessors(SongsDownloader songsDownloader, BaseParser baseParser) {
        this.songsDownloader = songsDownloader;
        this.baseParser = baseParser;
    }

    public SongsProcessors() {

    }

    public SongsDownloader getSongsDownloader() {
        return songsDownloader;
    }

    public void setSongsDownloader(SongsDownloader songsDownloader) {
        this.songsDownloader = songsDownloader;
    }

    public BaseParser getBaseParser() {
        return baseParser;
    }

    public void setBaseParser(BaseParser baseParser) {
        this.baseParser = baseParser;
    }
}
