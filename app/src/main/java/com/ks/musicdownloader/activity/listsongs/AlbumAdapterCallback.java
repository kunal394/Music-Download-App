package com.ks.musicdownloader.activity.listsongs;

/**
 * Created by Kunal Singh(knl.singh) on 27-09-2018.
 */
public interface AlbumAdapterCallback {

    void setSongCheckedStatus(Integer songId, Boolean status, Integer checkedCount);

    void notifyAllChecked();
}
