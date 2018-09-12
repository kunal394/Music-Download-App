package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampParser;

import java.io.File;

public enum MusicSite implements MusicSiteService {

    BANDCAMP {
        @Override
        public boolean isUrlMatching(String url) {
            return RegexUtils.isRegexMatching(Constants.BANDCAMP_URL_REGEX, url);
        }

        @Override
        public BandcampParser getMusicParser(String url, DownloadCallback downloadCallback) {
            return new BandcampParser(url, downloadCallback);
        }

        @Override
        public String createFilePath(String artist, String album, String song) {
            return artist + File.separator + album + File.separator + song;
        }
    }
}
