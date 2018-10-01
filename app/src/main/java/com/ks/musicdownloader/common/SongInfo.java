package com.ks.musicdownloader.common;

import android.os.Parcel;
import android.os.Parcelable;

public class SongInfo implements Parcelable {

    private static final String TAG = SongInfo.class.getSimpleName();

    private Integer id;

    private String name;

    private String url;

    private String album;

    private boolean checked;

    public SongInfo(Integer id, String name, String url, String album, Boolean checked) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.album = album;
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "SongInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", checked=" + checked +
                ", album='" + album + '\'' +
                '}';
    }

    private SongInfo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        url = in.readString();
        album = in.readString();
        checked = in.readByte() != 0;
    }

    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel in) {
            return new SongInfo(in);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(album);
        dest.writeByte((byte) (checked ? 1:0));
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
