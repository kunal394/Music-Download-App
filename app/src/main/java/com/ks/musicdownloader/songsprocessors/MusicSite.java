package com.ks.musicdownloader.songsprocessors;

import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.SubMenu;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.Utils.StringUtils;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.songsprocessors.bandcamp.BandcampParser;

import java.io.File;

public enum MusicSite implements MusicSiteHelper {

    BANDCAMP {
        @Override
        public boolean isUrlMatching(String url) {
            return RegexUtils.isRegexMatching(Constants.BANDCAMP_URL_REGEX, url);
        }

        @Override
        public BandcampParser getMusicParser(String url) {
            return new BandcampParser(url);
        }

        @Override
        public String createFilePath(String externalDir, String album, String song, String artist) {
            return StringUtils.add(externalDir, File.separator, artist, File.separator
                    , album, File.separator, song, Constants.MP3_EXTENSION);
        }

        @Override
        public void createNavMenu(NavigationView navView, ArtistInfo artistInfo) {

            // create artist menu
            Menu menu = navView.getMenu();
            SubMenu artistSubMenu = menu.addSubMenu(R.string.artist_group_id, Menu.NONE,
                    Constants.ARTIST_INFO_MENU_PRIORITY, R.string.artist_info);
            artistSubMenu.add(R.string.artist_group_id, R.string.artist_item_id,
                    Constants.ARTIST_INFO_MENU_PRIORITY, artistInfo.getArtist())
                    .setIcon(R.drawable.ic_album_black_24dp);
            artistSubMenu.setGroupCheckable(R.string.artist_group_id, true, true);

            // create album menu
            SubMenu albumSubMenu = menu.addSubMenu(R.string.album_group_id, Menu.NONE,
                    Constants.ALBUM_INFO_MENU_PRIORITY, R.string.album_info);
            for (String album : artistInfo.getAlbumInfo().keySet()) {
                albumSubMenu.add(R.string.album_group_id, R.string.album_item_id, Constants.ALBUM_INFO_MENU_PRIORITY, album)
                        .setIcon(R.drawable.ic_album_black_24dp);
            }
            albumSubMenu.setGroupCheckable(R.string.album_group_id, true, true);
        }
    }
}
