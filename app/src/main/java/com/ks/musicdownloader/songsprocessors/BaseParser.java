package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.common.ArtistInfo;
import com.ks.musicdownloader.common.DownloadCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class BaseParser {

    private String url;

    private DownloadCallback downloadCallback;

    public BaseParser(String url, DownloadCallback downloadCallback) {
        if (url.endsWith("/")) {
            // remove '/' from the end of the url
            url = url.substring(0, url.length() - 1);
        }
        this.url = url;
        this.downloadCallback = downloadCallback;
    }

    public abstract ArtistInfo parseArtistInfo(Boolean defaultChecked) throws IOException;

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
