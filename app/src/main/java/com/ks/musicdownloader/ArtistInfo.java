package com.ks.musicdownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArtistInfo {

    private String artist;

    private HashMap<String, List<SongInfo>> albumInfo;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public HashMap<String, List<SongInfo>> getAlbumInfo() {
        if (albumInfo == null) {
            return new HashMap<>();
        }
        return albumInfo;
    }

    public void setAlbumInfo(HashMap<String, List<SongInfo>> albumInfo) {
        this.albumInfo = albumInfo;
    }

    public void addSongInfoToAlbum(SongInfo songInfo, String album) {
        if (albumInfo == null) {
            albumInfo = new HashMap<>();
        }
        List<SongInfo> songInfoForAlbum = getSongInfoForAlbum(album);
        songInfoForAlbum.add(songInfo);
    }

    private List<SongInfo> getSongInfoForAlbum(String album) {
        if (albumInfo == null) {
            return new ArrayList<>();
        }

        List<SongInfo> songInfos = albumInfo.get(album);
        return songInfos == null ? new ArrayList<SongInfo>() : songInfos;
    }
}
