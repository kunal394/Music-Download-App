package com.ks.musicdownloader.activity.common;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.Utils.FileUtils;
import com.ks.musicdownloader.Utils.ToastUtils;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("DanglingJavadoc")
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREF_NAME);
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);
        getPreferenceManager().findPreference(Constants.PREF_DEFAULT_SONGS_FOLDER_KEY).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case Constants.PREF_DEFAULT_SONGS_FOLDER_KEY:
                displayExplorer();
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.d(TAG, "Result is not RESULT_OK");
            return;
        }
        if (requestCode == Constants.SET_DEFAULT_FOLDER_REQUEST_CODE) {
            String folderPath = FileUtils.getFullPathFromTreeUri(data.getData(), getContext());
            if (!FileUtils.doesFolderExists(folderPath)) {
                return;
            }
            Log.d(TAG, "onActivityResult(): destinationFolderPreference, data: " +
                    data.getDataString() + " path: " + folderPath);
            CommonUtils.putPrefString(Objects.requireNonNull(getContext()), Constants.SETTINGS_PREF_NAME,
                    Constants.PREF_DEFAULT_SONGS_FOLDER_KEY, folderPath);
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void displayExplorer() {
        // TODO: 10-10-2018 not showing the current value of default folder in settings, need to see this
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

//        Uri selectedUri = Uri.parse(Constants.EXTERNAL_STORAGE_DIRECTORY);
//         not working with setDataAndType
//        intent.setDataAndType(selectedUri, "resource/folder");

        try {
            startActivityForResult(Intent.createChooser(intent, "Select Folder"), Constants.SET_DEFAULT_FOLDER_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error in starting activity for selecting folder. Error: " + e);
            ToastUtils.displayLongToast(getContext(), "Error in getting file manager!");
        }
    }

}
