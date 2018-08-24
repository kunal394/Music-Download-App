package com.ks.musicdownloader.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.NetworkUtils;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends Activity {

    public static final String DOWNLOAD_URL = "com.ks.musicdownloader.DOWNLOAD_URL";

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        if (NetworkUtils.isNetworkConnected(this)) {
            createIntentAndDelegateActivity();
        } else {
            displayNoInternetDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void displayNoInternetDialog() {
        // show a prompt notifying user that the internet is not available and return,
        // otherwise start the other activity by passing the url provided by the user with the intent
        new AlertDialog.Builder(this)
                .setTitle("No Intenet Connection")
                .setMessage("It seems you are not connected to the internet. Please connect and try again!")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setIcon(R.drawable.ic_launcher_background)
                .show();
    }

    private void createIntentAndDelegateActivity() {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);
        String url = editText.getText().toString();
        if (URLUtil.isValidUrl(url)) {
            intent.putExtra(DOWNLOAD_URL, url);
            startActivity(intent);
        } else {
            Log.d(TAG, "Error with the url: " + url);
            displayInvalidURLDialog();
        }
    }

    private void displayInvalidURLDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Invalid URL")
                .setMessage("The url provided is not valid. Please try again with a valid url!")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setIcon(R.drawable.ic_launcher_background)
                .show();
    }
}
