package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.DownloadCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class BaseParser {

    private String url;

    private DownloadCallback downloadCallback;

    public BaseParser(String url, DownloadCallback downloadCallback) {
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

    protected Document fetchDocumentFromUrl(String Url) throws IOException {
        return Jsoup.connect(Url).get();
    }
}
