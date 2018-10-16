package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.activity.common.ArtistInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class BaseParser {

    private String url;


    public BaseParser(String url) {
        if (url.endsWith("/")) {
            // remove '/' from the end of the url
            url = url.substring(0, url.length() - 1);
        }
        this.url = url;
    }

    public abstract ArtistInfo parseArtistInfo(Boolean defaultChecked) throws IOException;

    protected String getUrl() {
        return url;
    }

    protected Document fetchDocumentFromUrl(String Url) throws IOException {
        return Jsoup.connect(Url).get();
    }
}
