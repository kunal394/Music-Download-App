package com.ks.musicdownloader.songsprocessors;

import com.ks.musicdownloader.DownloadCallback;

public interface MusicSiteService {

    /**
     * This method is responsible for checking if a url belongs to a particular website or not.
     * Also if it does, then whether it exists or not by making an http call and checking the
     * response code.
     *
     * @param url url to check
     * @return whether a url belongs to this site
     */
    boolean isUrlMatching(String url);

    SongsParser getMusicParser(String url, DownloadCallback downloadCallback);

}
