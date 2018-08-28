package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.Constants;

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

    public String getSite(String url) {
        for (MusicSite musicSite : MusicSite.values()) {
            if (musicSite.isUrlMatching(url)) {
                return musicSite.name();
            }
        }
        return Constants.EMPTY_STRING;
    }
}
