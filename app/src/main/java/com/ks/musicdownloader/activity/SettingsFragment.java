package com.ks.musicdownloader.activity;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.common.Constants;

public class SettingsFragment extends PreferenceFragmentCompat {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREF_NAME);
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);
    }
}
