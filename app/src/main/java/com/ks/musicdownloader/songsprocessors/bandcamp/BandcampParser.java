package com.ks.musicdownloader.songsprocessors.bandcamp;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.songsprocessors.SongsParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String getRegexResult(String pattern, String text) {
        String matched = "";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        if (m.find()) {
            matched = m.group();
        }
        return matched;
    }

    private String getTralbumData(String url) throws IOException {
        String regex = "(?<=var\\sTralbumData\\s=\\s)(.)*?(?=};)";
        Document document = Jsoup.connect(url).get();
        String scriptData = document.getElementsByTag("script").toString().replaceAll("\\s", " ");
        return getRegexResult(regex, scriptData);
    }
}
