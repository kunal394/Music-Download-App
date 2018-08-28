package com.ks.musicdownloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.Utils.ToastUtils;
import com.ks.musicdownloader.songsprocessors.SongsFactory;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SongsFactory songsFactory;

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        if (NetworkUtils.isNetworkConnected(this)) {
            createIntentAndDelegateActivity();
        } else {
            displayNoInternetToast();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void createIntentAndDelegateActivity() {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);
        String url = editText.getText().toString();
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            Log.d(TAG, "Error with the url: " + url);
            displayInvalidUrlToast();
            return;
        }
        String siteName = getSongsFactory().getSite(url);
        if (!siteName.equals("")) {
            intent.putExtra(Constants.DOWNLOAD_URL, url);
            intent.putExtra(Constants.SITE_NAME, siteName);
            startActivity(intent);
        } else {
            Log.d(TAG, "Unknown site: " + url);
            displayUnsupportedSiteToast();
        }
    }

    private void displayUnsupportedSiteToast() {
        ToastUtils.displayToast(this, Constants.UNSUPPORTED_SITE, Toast.LENGTH_LONG);
    }

    private void displayInvalidUrlToast() {
        ToastUtils.displayToast(this, Constants.INVALID_URL, Toast.LENGTH_LONG);
    }

    private void displayNoInternetToast() {
        ToastUtils.displayToast(this, Constants.NO_INTERNET, Toast.LENGTH_LONG);
    }

    private SongsFactory getSongsFactory() {
        if (songsFactory == null) {
            songsFactory = SongsFactory.getInstance();
        }
        return songsFactory;
    }
}
