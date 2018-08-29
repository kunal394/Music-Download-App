package com.ks.musicdownloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.Utils.ToastUtils;
import com.ks.musicdownloader.songsprocessors.SongsFactory;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SongsFactory songsFactory;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean networkConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkConnected = false;
        createNetworkCallback();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
    }

    // when display message activity opens, this function gets called
    // so unregister the network callback here and then register again in onStart
    @Override
    protected void onStop() {
        super.onStop();
        NetworkUtils.unRegReceiverForConnectionValidationOnly(this, networkCallback);
        networkCallback = null;
    }

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        hideKeyboard();
        if (networkConnected) {
            validateUrlAndStartActivityIfValid();
        } else {
            displayNoInternetToast();
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void hideKeyboard() {

    }

    private void validateUrlAndStartActivityIfValid() {

    }

    private void createIntentAndDelegateActivity() {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);
        String url = editText.getText().toString();
        if (Constants.EMPTY_STRING.equals(url)) {
            displayNoUrlProvidedToast();
            return;
        } else if (!RegexUtils.isAValidUrl(url)) {
            Log.d(TAG, "Error with the url: " + url);
            displayInvalidUrlToast();
            return;
        }

        url = prependHTTPSPartIfNotPresent(url);
        String siteName = getSongsFactory().getSite(url);
        if (!Constants.EMPTY_STRING.equals(siteName)) {
            intent.putExtra(Constants.DOWNLOAD_URL, url);
            intent.putExtra(Constants.SITE_NAME, siteName);
            startActivity(intent);
        } else {
            Log.d(TAG, "Unknown site: " + url);
            displayUnsupportedSiteToast();
        }
    }

    private String prependHTTPSPartIfNotPresent(String url) {
        if (!RegexUtils.startsWithHTTP(url) && !RegexUtils.startsWithHTTPS(url)) {
            url = Constants.URL_HTTPS_PART + url;
        }
        return url;
    }

    private void createNetworkCallback() {
        Log.d(TAG, "createNetworkCallback()");
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                networkConnected = true;
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                networkConnected = false;
            }
        };
    }

    private void displayNoInternetToast() {
        ToastUtils.displayToast(this, Constants.NO_INTERNET, Toast.LENGTH_LONG);
    }

    private void displayNoUrlProvidedToast() {
        ToastUtils.displayToast(this, Constants.NO_URL_PROVIDED, Toast.LENGTH_LONG);
    }

    private void displayInvalidUrlToast() {
        ToastUtils.displayToast(this, Constants.INVALID_URL, Toast.LENGTH_LONG);
    }

    private void displayUnsupportedSiteToast() {
        ToastUtils.displayToast(this, Constants.UNSUPPORTED_SITE, Toast.LENGTH_LONG);
    }

    private SongsFactory getSongsFactory() {
        if (songsFactory == null) {
            songsFactory = SongsFactory.getInstance();
        }
        return songsFactory;
    }
}
