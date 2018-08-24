package com.ks.musicdownloader.songsprocessors.bandcamp;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.SongsParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class BandcampParser extends SongsParser {

    private static final String TAG = BandcampParser.class.getSimpleName();
    private String TEST_URL = "https://allthemwitches.bandcamp.com/album/our-mother-electricity";

    public BandcampParser(String url, DownloadCallback downloadCallback) {
        super(url, downloadCallback);
    }

    @Override
    public ArtistInfo parseArtistInfo() throws IOException {
        String tralbumData = getTralbumData(getUrl());
        return new ArtistInfo();
    }

    private String getTralbumData(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        String scriptData = document.getElementsByTag("script").toString().replaceAll("\\s", " ");
        return RegexUtils.getFirstRegexResult(Constants.BANDCAMP_TRALBUM_REGEX, scriptData);
    }
}
