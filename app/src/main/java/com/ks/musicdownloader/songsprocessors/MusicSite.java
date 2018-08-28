package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampParser;

public enum MusicSite implements MusicSiteService {

    BANDCAMP {
        @Override
        public boolean isUrlMatching(String url) {
            return RegexUtils.isRegexMatching(Constants.BANDCAMP_URL_REGEX, url);
        }

        @Override
        public SongsParser getMusicParser(String url, DownloadCallback downloadCallback) {
            return new BandcampParser(url, downloadCallback);
        }
    }
}
