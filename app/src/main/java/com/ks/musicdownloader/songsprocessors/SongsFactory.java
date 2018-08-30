package com.ks.musicdownloader.songsprocessors;

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

    public MusicSite getSite(String url) {
        for (MusicSite musicSite : MusicSite.values()) {
            if (musicSite.isUrlMatching(url)) {
                return musicSite;
            }
        }
        return null;
    }
}
