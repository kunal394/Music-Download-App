package com.ks.musicdownloader.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    private static final String TAG = PrefUtils.class.getSimpleName();

    private PrefUtils() {
        // enforcing non-instantiability since it is a utility class
    }

    public static Boolean getPrefBoolean(Context context, String prefName, String prefKey, boolean defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(prefKey, defValue);
    }

    public static String getPrefString(Context context, String prefName, String prefKey, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefKey, defaultValue);
    }

    public static void putPrefString(Context context, String prefName, String prefKey, String prefValue) {
        SharedPreferences pref = context.getSharedPreferences(prefName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(prefKey, prefValue);
        editor.apply();
    }

    public static String putPrefStringIfNull(Context context, String prefName, String prefKey, String defaultValue) {
        String currentVal = getPrefString(context, prefName, prefKey, null);
        if (currentVal == null) {
            putPrefString(context, prefName, prefKey, defaultValue);
            currentVal = defaultValue;
        }
        return currentVal;
    }

    public static Integer getPrefInt(Context context, String prefName, String prefKey, Integer defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(prefKey, defaultValue);
    }

    public static void putPrefInt(Context context, String prefName, String prefKey, Integer prefValue) {
        SharedPreferences pref = context.getSharedPreferences(prefName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(prefKey, prefValue);
        editor.apply();
    }
}
