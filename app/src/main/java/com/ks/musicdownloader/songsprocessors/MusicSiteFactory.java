package com.ks.musicdownloader.songsprocessors;

public class MusicSiteFactory {

    private static final String TAG = MusicSiteFactory.class.getSimpleName();

    private static MusicSiteFactory musicSiteFactory = null;

    private MusicSiteFactory() {

    }

    public static MusicSiteFactory getInstance() {
        if (musicSiteFactory == null) {
            musicSiteFactory = new MusicSiteFactory();
        }
        return musicSiteFactory;
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
