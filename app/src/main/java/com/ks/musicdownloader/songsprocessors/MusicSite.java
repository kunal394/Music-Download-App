package com.ks.musicdownloader.songsprocessors;

import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.SubMenu;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampParser;

import java.io.File;

public enum MusicSite implements MusicSiteService {

    BANDCAMP {
        @Override
        public boolean isUrlMatching(String url) {
            return RegexUtils.isRegexMatching(Constants.BANDCAMP_URL_REGEX, url);
        }

        @Override
        public BandcampParser getMusicParser(String url) {
            return new BandcampParser(url, null);
        }

        @Override
        public String createFilePath(String artist, String album, String song) {
            return artist + File.separator + album + File.separator + song + Constants.MP3_EXTENSION;
        }

        @Override
        public void createNavMenu(NavigationView navView, ArtistInfo artistInfo) {

            // create artist menu
            Menu menu = navView.getMenu();
            SubMenu artistSubMenu = menu.addSubMenu(R.string.artist_group_id, Menu.NONE, 100, R.string.artist_info);
            artistSubMenu.add(R.string.artist_group_id, Menu.NONE, 100, artistInfo.getArtist());
            artistSubMenu.setGroupCheckable(R.string.artist_group_id, true, true);

            // create album menu
            SubMenu albumSubMenu = menu.addSubMenu(R.string.album_group_id, Menu.NONE, 105, R.string.album_info);
            for (String album : artistInfo.getAlbumInfo().keySet()) {
                albumSubMenu.add(R.string.album_group_id, Menu.NONE, 105, album);
            }
            albumSubMenu.setGroupCheckable(R.string.album_group_id, true, true);
        }
    }
}
