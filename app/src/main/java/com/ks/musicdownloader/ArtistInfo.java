package com.ks.musicdownloader;

import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArtistInfo implements Serializable {

    private String artist;

    // album name to list of ids of songs
    private HashMap<String, List<Integer>> albumInfo;

    // song id to songInfo
    private SparseArray<SongInfo> songsMap;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void addSongsInfoToAlbum(List<SongInfo> songsInfo, String album) {
        if (albumInfo == null) {
            albumInfo = new HashMap<>();
        }

        if (songsMap == null) {
            songsMap = new SparseArray<>();
        }
        List<Integer> songsIds = new ArrayList<>();
        for (SongInfo songInfo : songsInfo) {
            songsIds.add(songInfo.getId());
            songsMap.put(songInfo.getId(), songInfo);
        }
        List<Integer> songsIdsForAlbum = getSongIdsForAlbum(album);
        songsIdsForAlbum.addAll(songsIds);
        albumInfo.put(album, songsIdsForAlbum);
    }

    private List<Integer> getSongIdsForAlbum(String album) {
        if (albumInfo == null) {
            return new ArrayList<>();
        }

        List<Integer> songsIds = albumInfo.get(album);
        return songsIds == null ? new ArrayList<Integer>() : songsIds;
    }

    public HashMap<String, List<Integer>> getAlbumInfo() {
        return albumInfo;
    }

    public SparseArray<SongInfo> getSongsMap() {
        if (songsMap == null) {
            songsMap = new SparseArray<>();
        }
        return songsMap;
    }
}
