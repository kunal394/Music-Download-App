package com.ks.musicdownloader.songsprocessors;

import android.support.design.widget.NavigationView;

import com.ks.musicdownloader.common.ArtistInfo;

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

    BaseParser getMusicParser(String url);

    String createFilePath(String artist, String album, String song);

    void createNavMenu(NavigationView navView, ArtistInfo artistInfo);

}
