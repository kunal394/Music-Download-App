package com.ks.musicdownloader.Utils;

import android.util.Log;

/**
 * Created by Kunal Singh(knl.singh) on 17-10-2018.
 */
public class LogUtils {

    private static final String TAG = "MusicDownloader";

    private LogUtils() {
        // enforcing non-instantiability since it is a utility class
    }

    public static void d(String classTag, String message) {
        Log.d(TAG, StringUtils.add(classTag, ": ", message));
    }
}
