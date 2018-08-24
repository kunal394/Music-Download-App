package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampDownloader;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampParser;

public class SongsFactory {

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
            songsProcessors.setSongsParser(new BandcampParser(url, downloadCallbackForParser));
            songsProcessors.setSongsDownloader(new BandcampDownloader(downloadCallbackForDownloader));
        } else {
            songsProcessors = null;
        }
        return songsProcessors;
    }

}
