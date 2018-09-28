package com.ks.musicdownloader;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistInfo implements Parcelable {

    private static final String TAG = ArtistInfo.class.getSimpleName();

    private String artist;

    // album name to list of ids of songs
    private HashMap<String, List<Integer>> albumInfo;

    /**
     * to be used only ui display logic, don't use for downloading logic
     */
    private HashMap<String, Boolean> albumCheckedStatus;

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

    public boolean getAlbumCheckedStatus(String album) {
        return albumCheckedStatus.get(album);
    }

    public HashMap<String, Boolean> getAlbumCheckedStatus() {
        if (albumCheckedStatus == null) {
            initializeAlbumCheckedStatus();
        }
        return albumCheckedStatus;
    }

    public void setAlbumCheckedStatus(String album, Boolean checked) {
        albumCheckedStatus.put(album, checked);
        for (Integer songId : albumInfo.get(album)) {
            songsMap.get(songId).setChecked(checked);
        }
    }

    // TODO: 28-09-2018 to be used for the first time based on user settings
    private void initializeAlbumCheckedStatus() {
        if (albumCheckedStatus == null) {
            albumCheckedStatus = new HashMap<>();
        }

        for (String album : albumInfo.keySet()) {
            albumCheckedStatus.put(album, false);
        }
    }

    public void setSongCheckedStatus(String album, Integer songId, Boolean status) {
        Log.d(TAG, "setSongCheckedStatus() ");
        SongInfo songInfo = getSongsMap().get(songId);
        songInfo.setChecked(status);
        if (status.equals(false)) {
            Log.d(TAG, "setSongCheckedStatus() unchecking song: " + songInfo.getName());
            albumCheckedStatus.put(album, false);
        }
    }

    public ArrayList<SongInfo> getSongsList(String album) {
        getAlbumInfo();
        ArrayList<SongInfo> songInfoList = new ArrayList<>();
        List<Integer> songIds = albumInfo.get(album);
        for (Integer songId : songIds) {
            songInfoList.add(songsMap.get(songId));
        }
        return songInfoList;
    }

    public boolean getArtistCheckedStatus() {
        getAlbumCheckedStatus();
        for (String album : albumInfo.keySet()) {
            if (!getAlbumCheckedStatus(album)) {
                return false;
            }
        }
        return true;
    }

    public boolean flipAlbumCheckedStatus(String album) {
        boolean newStatus = !albumCheckedStatus.get(album);
        albumCheckedStatus.put(album, newStatus);
        for (Integer songId : albumInfo.get(album)) {
            songsMap.get(songId).setChecked(newStatus);
        }
        return newStatus;
    }

    public boolean flipArtistCheckedStatus() {
        boolean newStatus = !getArtistCheckedStatus();
        setArtistCheckedStatus(newStatus);
        return newStatus;
    }

    public void setArtistCheckedStatus(boolean status) {
        for (Map.Entry<String, List<Integer>> albumEntry : albumInfo.entrySet()) {
            String album = albumEntry.getKey();
            albumCheckedStatus.put(album, status);
            List<Integer> songIds = albumEntry.getValue();
            for (Integer songId : songIds) {
                songsMap.get(songId).setChecked(status);
            }
        }
    }

    public Integer getCheckedSongsCountInAlbum(String album) {
        Integer checkedCount = 0;
        for (Integer songId : albumInfo.get(album)) {
            if (songsMap.get(songId).isChecked()) {
                checkedCount++;
            }
        }
        return checkedCount;
    }

    public Integer getCheckedAlbumCount() {
        Integer checkedCount = 0;
        for (Boolean status : albumCheckedStatus.values()) {
            if (status) {
                checkedCount++;
            }
        }
        return checkedCount;
    }
}
