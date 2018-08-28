package com.ks.musicdownloader.Utils;

import android.content.Context;
import android.widget.Toast;

@SuppressWarnings("DanglingJavadoc")
public class ToastUtils {

    public static void displayToast(Context context, String message, int toastLength) {
        createToast(context, message, toastLength).show();
    }

    /******************Private************************************/
    /******************Methods************************************/

    private static Toast createToast(Context context, String message, int toastLength) {
        return Toast.makeText(context, message, toastLength);
    }
}
