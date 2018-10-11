package com.ks.musicdownloader.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.main.MainActivity;

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
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.d(TAG, "sendNotification(): noti manager null!!");
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    Constants.DEFAULT_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(Constants.DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        // TODO: 11-10-2018 not working, back button just closes the app
        stackBuilder.addParentStack(MainActivity.class);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(Constants.PENDING_INTENT_DEFAULT_REQ_CODE,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(iconResourceId);
        builder.setContentIntent(pendingIntent);
        builder.setTimeoutAfter(Constants.DEFAULT_NOTIFICATION_TIMEOUT);

        Notification notification = builder.build();
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        NotificationManagerCompat.from(context).notify(id, notification);
    }
}
