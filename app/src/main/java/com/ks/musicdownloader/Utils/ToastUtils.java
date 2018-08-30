package com.ks.musicdownloader.Utils;

import android.content.Context;
import android.widget.Toast;

@SuppressWarnings("DanglingJavadoc")
public class ToastUtils {

    public static void displayLongToast(Context context, String message) {
        displayToast(context, message, Toast.LENGTH_LONG);
    }

    /******************Private************************************/
    /******************Methods************************************/

    private static void displayToast(Context context, String message, int toastLength) {
        createToast(context, message, toastLength).show();
    }

    private static Toast createToast(Context context, String message, int toastLength) {
        return Toast.makeText(context, message, toastLength);
    }
}
