package com.ks.musicdownloader.activity.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import com.ks.musicdownloader.R;

/**
 * Created by Kunal Singh(knl.singh) on 10-10-2018.
 */
public class DestinationFolderPreference extends Preference {

    private String defFolder;

    public DestinationFolderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // do custom stuff here, read attributes etc.
    }

    public DestinationFolderPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public DestinationFolderPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public DestinationFolderPreference(Context context) {
        this(context, null);
    }

    public String getDefFolder() {
        return defFolder;
    }

    public void setDefFolder(String defFolder) {
        this.defFolder = defFolder;
        // Save to Shared Preferences
        persistString(defFolder);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setDefFolder(restorePersistedValue ?
                getPersistedString(Constants.MUSIC_DIRECTORY) : (String) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }
}
