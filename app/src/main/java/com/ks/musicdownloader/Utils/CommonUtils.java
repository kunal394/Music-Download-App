package com.ks.musicdownloader.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    // works only when called from an activity
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Boolean getPrefBoolean(Context context, String prefName, String prefKey, boolean defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(prefKey, defValue);
    }

    public static void putPrefString(Context context, String prefName, String prefKey, String prefValue) {
        SharedPreferences pref = context.getSharedPreferences(prefName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(prefKey, prefValue);
        editor.apply();
    }

    public static String getPrefString(Context context, String prefName, String prefKey, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefKey, defaultValue);
    }

    public static String putPrefStringIfNull(Context context, String prefName, String prefKey, String defaultValue) {
        String currentVal = getPrefString(context, prefName, prefKey, null);
        if (currentVal == null) {
            putPrefString(context, prefName, prefKey, defaultValue);
            currentVal = defaultValue;
        }
        return currentVal;
    }

    public static boolean appInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : activityManager.getRunningAppProcesses()) {
            if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    || runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                Log.i("Foreground App: ", runningAppProcessInfo.processName);
                if (runningAppProcessInfo.processName.equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void sendNotification(Context context, String title, String body,
                                        String channelId, Intent intent, Integer id, Integer iconResourceId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(iconResourceId);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat.from(context).notify(id, notification);
    }
}
