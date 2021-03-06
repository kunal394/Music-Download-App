package com.ks.musicdownloader.songsprocessors;

import android.support.design.widget.NavigationView;

import com.ks.musicdownloader.activity.common.ArtistInfo;

public interface MusicSiteHelper {

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

    String createFilePath(String externalDir, String album, String song, String artist);

    void createNavMenu(NavigationView navView, ArtistInfo artistInfo);

}
