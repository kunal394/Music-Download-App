package com.ks.musicdownloader.songsprocessors;

import android.util.Log;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampDownloader;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampParser;

public class SongsFactory {

    private static final String TAG = SongsFactory.class.getSimpleName();

    private static SongsFactory songsFactory = null;

    private SongsFactory() {

    }

    public static SongsFactory getInstance() {
        if (songsFactory == null) {
            songsFactory = new SongsFactory();
        }
        return songsFactory;
    }

    public SongsProcessors getSongsProcessors(String url, DownloadCallback downloadCallbackForParser,
                                              DownloadCallback downloadCallbackForDownloader) {
        SongsProcessors songsProcessors = new SongsProcessors();
        if (RegexUtils.isRegexMatching(Constants.BANDCAMP_URL_REGEX, url)) {
            Log.d(TAG, "getSongsProcessors(): Bandcamp url detected. Url: " + url);
            songsProcessors.setSongsParser(new BandcampParser(url, downloadCallbackForParser));
            songsProcessors.setSongsDownloader(new BandcampDownloader(downloadCallbackForDownloader));
        } else {
            Log.d(TAG, "getSongsProcessors(): Unknown url. Url: " + url);
            songsProcessors = null;
        }
        return songsProcessors;
    }

}
