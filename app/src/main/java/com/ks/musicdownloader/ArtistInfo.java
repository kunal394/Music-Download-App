package com.ks.musicdownloader;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistInfo implements Parcelable {

    private String artist;

    // album name to list of ids of songs
    private HashMap<String, List<Integer>> albumInfo;

    // song id to songInfo
    private SparseArray<SongInfo> songsMap;

    public ArtistInfo() {
    }

    private ArtistInfo(Parcel in) {
        artist = in.readString();
        // Read album info
        getAlbumInfo();
        int albumInfoSize = in.readInt();
        for (int i = 0; i < albumInfoSize; i++) {
            String key = in.readString();
            List<Integer> value = new ArrayList<>();
            in.readList(value, null);
            albumInfo.put(key, value);
        }

        // Read song map
        getSongsMap();
        int songsMapSize = in.readInt();
        for (int i = 0; i < songsMapSize; i++) {
            int key = in.readInt();
            SongInfo value = in.readParcelable(SongInfo.class.getClassLoader());
            songsMap.put(key, value);
        }
    }

    public static final Creator<ArtistInfo> CREATOR = new Creator<ArtistInfo>() {
        @Override
        public ArtistInfo createFromParcel(Parcel in) {
            return new ArtistInfo(in);
        }

        @Override
        public ArtistInfo[] newArray(int size) {
            return new ArtistInfo[size];
        }
    };

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
        if (albumInfo == null) {
            albumInfo = new HashMap<>();
        }
        return albumInfo;
    }

    public SparseArray<SongInfo> getSongsMap() {
        if (songsMap == null) {
            songsMap = new SparseArray<>();
        }
        return songsMap;
    }

    @Override
    public String toString() {
        return "ArtistInfo{" +
                "artist='" + artist + '\'' +
                ", albumInfo=" + albumInfo.toString() +
                ", songsMap=" + songsMap.toString() +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artist);
        // Write album info
        getAlbumInfo();
        dest.writeInt(albumInfo.size());
        for (Map.Entry<String, List<Integer>> item : albumInfo.entrySet()) {
            dest.writeString(item.getKey());
            dest.writeList(item.getValue());
        }

        // Write song map
        getSongsMap();
        dest.writeInt(songsMap.size());
        for (int i = 0; i < songsMap.size(); i++) {
            int key = songsMap.keyAt(i);
            dest.writeInt(key);
            dest.writeParcelable(songsMap.get(key), flags);
        }
    }
}
