package com.ks.musicdownloader.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import com.ks.musicdownloader.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

@SuppressWarnings("DanglingJavadoc")
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static void regReceiverForConnectionValidationOnly(Context context, ConnectivityManager.NetworkCallback networkCallback) {
        Log.d(TAG, "regReceiverForConnectionValidationOnly");
        ConnectivityManager connMgr = getConnectivityManager(context);
        if (connMgr == null) {
            Log.d(TAG, "regReceiverForNetworkValidationOnly: Received null connMgr!! Can not register for network callback.");
            return;
        }
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connMgr.registerNetworkCallback(networkRequest, networkCallback);
    }

    public static void unRegReceiverForConnectionValidationOnly(Context context, ConnectivityManager.NetworkCallback networkCallback) {
        Log.d(TAG, "unRegReceiverForConnectionValidationOnly");
        ConnectivityManager connMgr = getConnectivityManager(context);
        if (connMgr == null) {
            Log.d(TAG, "regReceiverForNetworkValidationOnly: Received null connMgr!! Can not register for network callback.");
            return;
        }
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connMgr.registerNetworkCallback(networkRequest, networkCallback);
    }

    public static void regNetworkReceiver(Context context, NetworkRequest networkRequest,
                                          ConnectivityManager.NetworkCallback networkCallback) {
        ConnectivityManager connMgr = getConnectivityManager(context);
        if (connMgr == null) {
            Log.d(TAG, "regNetworkReceiver: Received null connMgr!! Can not register for network callback.");
            return;
        }
        connMgr.registerNetworkCallback(networkRequest, networkCallback);
    }

    public static boolean doesUrlExists(String url) throws IOException {
        if (RegexUtils.startsWithHTTP(url)) {
            return doesHTTPUrlExists(url);
        } else if (RegexUtils.startsWithHTTPS(url)) {
            return doesHTTPSUrlExists(url);
        }
        return false;
    }

    /******************Private************************************/
    /******************Methods************************************/

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connMgr = getConnectivityManager(context);
        return (connMgr != null ? connMgr.getActiveNetworkInfo() : null);
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static boolean doesHTTPUrlExists(String url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setRequestMethod(Constants.REQUEST_HEAD);
        httpURLConnection.connect();
        int responseCode = httpURLConnection.getResponseCode();
        return false;
    }

    private static boolean doesHTTPSUrlExists(String url) throws IOException {
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
        httpsURLConnection.setRequestMethod(Constants.REQUEST_HEAD);
        httpsURLConnection.connect();
        int responseCode = httpsURLConnection.getResponseCode();
        return false;
    }
}
