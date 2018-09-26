package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.Constants;
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
        public BandcampParser getMusicParser(String url) {
            return new BandcampParser(url, null);
        }

        @Override
        public String createFilePath(String artist, String album, String song) {
            return artist + File.separator + album + File.separator + song + Constants.MP3_EXTENSION;
        }
    }
}
