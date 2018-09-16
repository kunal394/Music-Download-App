package com.ks.musicdownloader.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private TextView progressBarTextView;
    private Handler handler;
    private String musicSite;

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
        progressBarTextView = findViewById(R.id.progressBarText);
        broadcastReceiver = new ParserBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        checkForPermissions();
        super.onStart();
        init();
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

    private void init() {
        networkConnected = false;
        createNetworkCallback();
        createHandler();
        registerBroadcastReceiver();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
        createUrlValidatorListener();
    }

    private void createHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage()");
                super.handleMessage(msg);
                switch (msg.what) {
                    case Constants.VALIDATING_PROGRESS:
                        validatorProgressBar.setVisibility(View.VISIBLE);
                        progressBarTextView.setText(R.string.validatorProgressText);
                        break;
                    case Constants.PARSING_PROGRESS:
                        progressBarTextView.setText(R.string.parsingProgressText);
                        break;
                    case Constants.HIDE_PROGRESS_BAR:
                        validatorProgressBar.setVisibility(View.GONE);
                        break;
                    case Constants.PARSE_ERROR:
                        validatorProgressBar.setVisibility(View.GONE);
                        displayErrorToast(ValidationResult.PARSING_ERROR);
                        break;
                }
            }
        };
    }

    private void validateURL() {
        EditText editText = findViewById(R.id.editText);
        String url = editText.getText().toString();
        handler.sendEmptyMessage(Constants.VALIDATING_PROGRESS);
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
                if (validationResult.isValidResult()) {
                    handler.sendEmptyMessage(Constants.PARSING_PROGRESS);
                    musicSite = siteName;
                    Intent intent = new Intent(MainActivity.this, ParserService.class);
                    intent.putExtra(Constants.DOWNLOAD_URL, url);
                    intent.putExtra(Constants.MUSIC_SITE, siteName);
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

    private void createIntentAndDelegateActivity(ArtistInfo artistInfo) {
        Intent intent = new Intent(this, DisplayListActivity.class);
        intent.putExtra(Constants.MUSIC_SITE, musicSite);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, artistInfo);
        intent.putExtras(bundle);
        startActivity(intent);
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
                    if (Constants.PARSE_ERROR_NULL_INTENT.equals(errorMsg)) {
                        Log.wtf(TAG, "ParserBroadcastReceiver, onReceive() parser service received null intent!");
                    }
                    handler.sendEmptyMessage(Constants.PARSE_ERROR);
                    break;
                case Constants.PARSE_SUCCESS_ACTION_KEY:
                    ArtistInfo artistInfo = intent.getParcelableExtra(Constants.PARSE_SUCCESS_MESSAGE_KEY);
                    Log.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_SUCCESS_ACTION_KEY, artistInfo: "
                            + artistInfo.toString());
                    createIntentAndDelegateActivity(artistInfo);
                    break;
                default:
                    break;
            }
        }
    }
}
