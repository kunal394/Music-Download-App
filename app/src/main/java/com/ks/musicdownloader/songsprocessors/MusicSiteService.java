package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.DownloadCallback;

public interface MusicSiteService {

    boolean isUrlMatching(String url);

    SongsParser getMusicParser(String url, DownloadCallback downloadCallback);

}
