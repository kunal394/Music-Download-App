package com.ks.musicdownloader.songsprocessors;

import android.util.Log;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampParser;

import java.io.IOException;

public enum MusicSite implements MusicSiteService {

    BANDCAMP {
        @Override
        public boolean isUrlMatchingAndExists(String url) {
            boolean regexMatching = RegexUtils.isRegexMatching(Constants.BANDCAMP_URL_REGEX, url);
            boolean urlExists = false;
            try {
                urlExists = NetworkUtils.doesUrlExists(url);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("MusicSite", "isUrlMatchingAndExists(Bandcamp): Error: "
                        + e + " while checking if url exists for url: " + url);
            }
            return regexMatching && urlExists;
        }

        @Override
        public SongsParser getMusicParser(String url, DownloadCallback downloadCallback) {
            return new BandcampParser(url, downloadCallback);
        }
    }
}
