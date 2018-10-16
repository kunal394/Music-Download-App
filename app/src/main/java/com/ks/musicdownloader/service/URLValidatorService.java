package com.ks.musicdownloader.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ks.musicdownloader.Utils.PrefUtils;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.songsprocessors.MusicSite;
import com.ks.musicdownloader.songsprocessors.MusicSiteFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Kunal Singh(knl.singh) on 11-10-2018.
 */
@SuppressWarnings("DanglingJavadoc")
public class URLValidatorService extends IntentService {

    private static final String TAG = URLValidatorService.class.getSimpleName();

    private String url;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public URLValidatorService(String name) {
        super(name);
    }

    public URLValidatorService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent()");
        if (intent == null) {
            Log.d(TAG, "onHandleIntent(): Intent null!");
            sendErrorBroadCast(Constants.VALIDATE_ERROR_NULL_INTENT);
            return;
        }

        url = intent.getStringExtra(Constants.DOWNLOAD_URL);
        if (Constants.EMPTY_STRING.equals(url)) {
            Log.d(TAG, "onHandleIntent(): Empty url!");
            sendErrorBroadCast(Constants.NO_URL_PROVIDED_MESSAGE);
            return;
        } else if (!RegexUtils.isAValidUrl(url)) {
            Log.d(TAG, "onHandleIntent(): Invalid url: " + url);
            sendErrorBroadCast(Constants.INVALID_URL_MESSAGE);
            return;
        }

        url = RegexUtils.prependHTTPSPartIfNotPresent(url);
        MusicSite site = MusicSiteFactory.getInstance().getSite(url);
        if (null == site) {
            Log.d(TAG, "onHandleIntent(): Unknown site: " + url);
            sendErrorBroadCast(Constants.UNSUPPORTED_SITE_MESSAGE);
        } else {
            if (remoteUrlExists()) {
                Log.d(TAG, "onHandleIntent(): site: " + site.name());
                sendSuccessBroadcast();
                startParserService(site);
            } else {
                sendErrorBroadCast(Constants.NON_EXISTENT_URL_MESSAGE);
            }
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void sendErrorBroadCast(String error) {
        Log.d(TAG, "sendErrorBroadCast()");
        PrefUtils.putPrefInt(getApplicationContext(), Constants.SEARCH_PREF_NAME,
                Constants.PREF_PARSING_STATUS_KEY, Constants.PARSING_COMPLETE);
        Intent intent = new Intent();
        intent.setAction(Constants.VALIDATE_ERROR_ACTION_KEY);
        intent.putExtra(Constants.VALIDATE_ERROR_MESSAGE_KEY, error);
        sendBroadcast(intent);
    }

    private void sendSuccessBroadcast() {
        Log.d(TAG, "sendSuccessBroadcast()");
        PrefUtils.putPrefInt(getApplicationContext(), Constants.SEARCH_PREF_NAME,
                Constants.PREF_PARSING_STATUS_KEY, Constants.PARSING_PROGRESS);
        Intent intent = new Intent();
        intent.setAction(Constants.VALIDATE_SUCCESS_ACTION_KEY);
        sendBroadcast(intent);
    }

    private void startParserService(MusicSite site) {
        Log.d(TAG, "startParserService()");
        Intent intent = new Intent(getApplicationContext(), ParserService.class);
        intent.putExtra(Constants.DOWNLOAD_URL, url);
        intent.putExtra(Constants.MUSIC_SITE, site.name());
        getApplicationContext().startService(intent);
    }

    private boolean remoteUrlExists() {
        try {
            HttpURLConnection httpURLConnection;
            if (RegexUtils.startsWithHTTP(url)) {
                httpURLConnection = createHttpConObj();
            } else {
                httpURLConnection = createHttpsConObj();
            }
            httpURLConnection.setRequestMethod(Constants.REQUEST_HEAD);
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.connect();
            return HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error: " + e + " while validating url: " + url);
            return false;
        }
    }

    private HttpURLConnection createHttpConObj() throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    private HttpsURLConnection createHttpsConObj() throws IOException {
        return (HttpsURLConnection) new URL(url).openConnection();
    }
}
