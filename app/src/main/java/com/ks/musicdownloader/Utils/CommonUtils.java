package com.ks.musicdownloader.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    private CommonUtils() {
        // enforcing non-instantiability since it is a utility class
    }

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

    public static boolean appInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : activityManager.getRunningAppProcesses()) {
            if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    || runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                Log.i(TAG, "appInForeground: " + runningAppProcessInfo.processName);
                if (runningAppProcessInfo.processName.equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void sendNotification(Context context, String title, String body,
                                        String channelId, Intent intent,
                                        Integer notificationId, Integer iconResourceId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            LogUtils.d(TAG, "sendNotification(): noti manager null!!");
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
        notification.defaults |= Notification.DEFAULT_ALL;
        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

    public static void cancelNotification(Context context, Integer notiId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notiId);
        }
    }
}
