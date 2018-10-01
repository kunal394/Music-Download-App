package com.ks.musicdownloader.Utils;

import com.ks.musicdownloader.common.ArtistInfo;
import com.ks.musicdownloader.common.SongInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal Singh(knl.singh) on 01-10-2018.
 */
public class TestUtils {

    public static ArtistInfo createTestArtistInfo(Boolean defaultChecked) {
        ArtistInfo artistInfoTest = new ArtistInfo();
        artistInfoTest.setArtist("Artist");
        List<SongInfo> songInfoList;

        songInfoList = new ArrayList<>();
        songInfoList.add(new SongInfo(1, "Song1", "url", "Album1", defaultChecked));
        songInfoList.add(new SongInfo(2, "Song2", "url", "Album1", defaultChecked));
        songInfoList.add(new SongInfo(3, "Song3", "url", "Album1", defaultChecked));
        songInfoList.add(new SongInfo(4, "Song4", "url", "Album1", defaultChecked));
        songInfoList.add(new SongInfo(5, "Song5", "url", "Album1", defaultChecked));
        artistInfoTest.addSongsInfoToAlbum(songInfoList, "Album1");

        songInfoList = new ArrayList<>();
        songInfoList.add(new SongInfo(6, "Song1", "url", "Album2", defaultChecked));
        songInfoList.add(new SongInfo(7, "Song2", "url", "Album2", defaultChecked));
        songInfoList.add(new SongInfo(8, "Song3", "url", "Album2", defaultChecked));
        songInfoList.add(new SongInfo(9, "Song4", "url", "Album2", defaultChecked));
        songInfoList.add(new SongInfo(10, "Song5", "url", "Album2", defaultChecked));
        songInfoList.add(new SongInfo(11, "Song6", "url", "Album2", defaultChecked));
        songInfoList.add(new SongInfo(12, "Song7", "url", "Album2", defaultChecked));
        artistInfoTest.addSongsInfoToAlbum(songInfoList, "Album2");

        songInfoList = new ArrayList<>();
        songInfoList.add(new SongInfo(13, "Song1", "url", "Album3", defaultChecked));
        songInfoList.add(new SongInfo(14, "Song2", "url", "Album3", defaultChecked));
        artistInfoTest.addSongsInfoToAlbum(songInfoList, "Album3");

        songInfoList = new ArrayList<>();
        songInfoList.add(new SongInfo(15, "Song1", "url", "Album4", defaultChecked));
        songInfoList.add(new SongInfo(16, "Song2", "url", "Album4", defaultChecked));
        songInfoList.add(new SongInfo(17, "Song3", "url", "Album4", defaultChecked));
        songInfoList.add(new SongInfo(18, "Song4", "url", "Album4", defaultChecked));
        songInfoList.add(new SongInfo(19, "Song5", "url", "Album4", defaultChecked));
        songInfoList.add(new SongInfo(20, "Song6", "url", "Album4", defaultChecked));
        songInfoList.add(new SongInfo(21, "Song7", "url", "Album4", defaultChecked));
        songInfoList.add(new SongInfo(22, "Song8", "url", "Album4", defaultChecked));
        artistInfoTest.addSongsInfoToAlbum(songInfoList, "Album4");

        songInfoList = new ArrayList<>();
        songInfoList.add(new SongInfo(23, "Song1", "url", "Album5", defaultChecked));
        artistInfoTest.addSongsInfoToAlbum(songInfoList, "Album5");

        songInfoList = new ArrayList<>();
        songInfoList.add(new SongInfo(24, "Song1", "url", "Album6", defaultChecked));
        songInfoList.add(new SongInfo(25, "Song2", "url", "Album6", defaultChecked));
        songInfoList.add(new SongInfo(26, "Song3", "url", "Album6", defaultChecked));
        songInfoList.add(new SongInfo(27, "Song4", "url", "Album6", defaultChecked));
        artistInfoTest.addSongsInfoToAlbum(songInfoList, "Album6");

        songInfoList = new ArrayList<>();
        String veryLargeSongsName = "This is supposed to be a very large song " +
                "name which should extend to two lines";
        songInfoList.add(new SongInfo(28, veryLargeSongsName, "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(29, "Song2", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(30, "Song3", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(31, "Song4", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(32, "Song5", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(33, "Song6", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(34, "Song7", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(35, "Song8", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(36, "Song9", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(37, "Song10", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(38, "Song11", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(39, "Song12", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(40, "Song13", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(41, "Song14", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(42, "Song15", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(43, "Song16", "url", "Album7", defaultChecked));
        songInfoList.add(new SongInfo(44, "Song17", "url", "Album7", defaultChecked));
        artistInfoTest.addSongsInfoToAlbum(songInfoList, "Album7");
        artistInfoTest.initializeAlbumCheckedStatus(defaultChecked);
        return artistInfoTest;
    }
}
