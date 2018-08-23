package com.ks.musicdownloader;

import java.util.List;

public class AlbumInfo {

    private String album;

    private List<SongInfo> songsInfo;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public List<SongInfo> getSongsInfo() {
        return songsInfo;
    }

    public void setSongsInfo(List<SongInfo> songsInfo) {
        this.songsInfo = songsInfo;
    }
}
