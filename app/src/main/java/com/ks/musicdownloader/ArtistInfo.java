package com.ks.musicdownloader;

import java.util.List;

public class ArtistInfo {

    private String artist;

    private List<AlbumInfo> albumsInfo;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public List<AlbumInfo> getAlbumsInfo() {
        return albumsInfo;
    }

    public void setAlbumsInfo(List<AlbumInfo> albumsInfo) {
        this.albumsInfo = albumsInfo;
    }
}
