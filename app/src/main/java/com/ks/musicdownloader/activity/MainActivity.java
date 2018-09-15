package com.ks.musicdownloader.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.service.ParserService;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean networkConnected;
    private URLValidatorTaskListener urlValidatorTaskListener;
    private RelativeLayout validatorProgressBar;
    private BroadcastReceiver broadcastReceiver;

    /**
     * Called when the user taps the Send button
     */
    public void extractSongsFromURL(View view) {
        CommonUtils.hideKeyboard(this);
        if (networkConnected) {
            validateURL();
        } else {
            displayErrorToast(ValidationResult.NO_INTERNET);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validatorProgressBar = findViewById(R.id.urlValidatorProgressBar);
        broadcastReceiver = new ParserBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        checkForPermissions();
        super.onStart();
        networkConnected = false;
        createNetworkCallback();
        registerBroadcastReceiver();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
        createUrlValidatorListener();
    }

    // when display message activity opens, this function gets called
    // so unregister the network callback here and then register again in onStart
    @Override
    protected void onStop() {
        super.onStop();
        NetworkUtils.unRegReceiverForConnectionValidationOnly(this, networkCallback);
        unregisterReceiver(broadcastReceiver);
        networkCallback = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ValidationResult.NO_EXTERNAL_STORAGE_PERMISSION.displayToast(this);
                    finish();
                }
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void checkForPermissions() {
        ActivityCompat.requestPermissions(this, Constants.REQUIRED_PERMISSIONS, Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void validateURL() {
        EditText editText = findViewById(R.id.editText);
        String url = editText.getText().toString();
        displayValidatorProgressBar();
        new URLValidatorTask(url, urlValidatorTaskListener).execute();
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

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.PARSE_ERROR_ACTION_KEY);
        intentFilter.addAction(Constants.PARSE_SUCCESS_ACTION_KEY);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void createUrlValidatorListener() {
        urlValidatorTaskListener = new URLValidatorTaskListener() {
            @Override
            public void handleValidatorResult(ValidationResult validationResult, String url, String siteName) {
                hideValidatorProgressBar();
                if (validationResult.isValidResult()) {
                    // createIntentAndDelegateActivity(url, siteName);
                    Intent intent = new Intent(MainActivity.this, ParserService.class);
                    intent.putExtra(Constants.DOWNLOAD_URL, url);
                    startService(intent);
                } else {
                    displayErrorToast(validationResult);
                }
            }
        };
    }

    private void displayErrorToast(ValidationResult validationResult) {
        validationResult.displayToast(this);
    }

    private void createIntentAndDelegateActivity(String url, String siteName) {
        Intent intent = new Intent(this, DisplayListActivity.class);
        intent.putExtra(Constants.DOWNLOAD_URL, url);
        intent.putExtra(Constants.MUSIC_SITE, siteName);
        startActivity(intent);
    }

    private void displayValidatorProgressBar() {
        validatorProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideValidatorProgressBar() {
        validatorProgressBar.setVisibility(View.GONE);
    }

    private class ParserBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "ParserBroadcastReceiver, onReceive()");
            String parseResult = intent.getAction();
            if (parseResult == null || parseResult.equals(Constants.EMPTY_STRING)) {
                return;
            }
            switch (parseResult) {
                case Constants.PARSE_ERROR_ACTION_KEY:
                    String errorMsg = intent.getStringExtra(Constants.PARSE_ERROR_MESSAGE_KEY);
                    Log.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_ERROR_ACTION_KEY, parse error: " + errorMsg);
                    // TODO: 16-09-2018 display error message here
                    if (Constants.PARSE_ERROR_NULL_INTENT.equals(errorMsg)) {
                        // should never happen
                    } else {
                        // display parse error message
                    }
                    break;
                case Constants.PARSE_SUCCESS_ACTION_KEY:
                    ArtistInfo artistInfo = (ArtistInfo) intent.getSerializableExtra(Constants.PARSE_SUCCESS_MESSAGE_KEY);
                    Log.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_SUCCESS_ACTION_KEY, artistInfo: "
                            + artistInfo.toString());
                    // TODO: 16-09-2018 start the next activity here with the artist info
                    break;
            }
        }
    }
}
