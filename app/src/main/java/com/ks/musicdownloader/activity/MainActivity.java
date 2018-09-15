package com.ks.musicdownloader.activity;

import android.content.Intent;
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

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.Utils.NetworkUtils;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean networkConnected;
    private URLValidatorTaskListener urlValidatorTaskListener;
    private RelativeLayout validatorProgressBar;

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        CommonUtils.hideKeyboard(this);
        if (networkConnected) {
            validateUrlAndStartActivityIfValid();
        } else {
            displayErrorToast(ValidationResult.NO_INTERNET);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validatorProgressBar = findViewById(R.id.urlValidatorProgressBar);
    }

    @Override
    protected void onStart() {
        checkForPermissions();
        super.onStart();
        networkConnected = false;
        createNetworkCallback();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
        createUrlValidatorListener();
    }

    // when display message activity opens, this function gets called
    // so unregister the network callback here and then register again in onStart
    @Override
    protected void onStop() {
        super.onStop();
        NetworkUtils.unRegReceiverForConnectionValidationOnly(this, networkCallback);
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

    private void validateUrlAndStartActivityIfValid() {
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

    private void createUrlValidatorListener() {
        urlValidatorTaskListener = new URLValidatorTaskListener() {
            @Override
            public void handleValidatorResult(ValidationResult validationResult, String url, String siteName) {
                hideValidatorProgressBar();
                if (validationResult.isValidResult()) {
                    createIntentAndDelegateActivity(url, siteName);
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
        intent.putExtra(Constants.SITE_NAME, siteName);
        startActivity(intent);
    }

    private void displayValidatorProgressBar() {
        validatorProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideValidatorProgressBar() {
        validatorProgressBar.setVisibility(View.GONE);
    }
}
