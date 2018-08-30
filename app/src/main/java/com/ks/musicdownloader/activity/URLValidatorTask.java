package com.ks.musicdownloader.activity;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.Utils.RegexUtils;
import com.ks.musicdownloader.songsprocessors.MusicSite;
import com.ks.musicdownloader.songsprocessors.SongsFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class URLValidatorTask extends AsyncTask<Void, Void, Pair<ValidationResult, String>> {

    private static final String TAG = URLValidatorTask.class.getSimpleName();

    private WeakReference<URLValidatorTaskListener> urlValidatorTaskListenerWeakReference;
    private String url;
    private static SongsFactory songsFactory;

    URLValidatorTask(String url, URLValidatorTaskListener urlValidatorTaskListener) {
        this.urlValidatorTaskListenerWeakReference = new WeakReference<>(urlValidatorTaskListener);
        this.url = url;
    }

    @Override
    protected Pair<ValidationResult, String> doInBackground(Void... voids) {
        if (Constants.EMPTY_STRING.equals(url)) {
            return new Pair<>(ValidationResult.NO_URL_PROVIDED, null);
        } else if (!RegexUtils.isAValidUrl(url)) {
            Log.d(TAG, "Error with the url: " + url);
            return new Pair<>(ValidationResult.INVALID_URL, null);
        }

        url = RegexUtils.prependHTTPSPartIfNotPresent(url);
        MusicSite site = getSongsFactory().getSite(url);
        if (null == site) {
            Log.d(TAG, "Unknown site: " + url);
            return new Pair<>(ValidationResult.UNSUPPORTED_SITE, null);
        } else {
            if (remoteUrlExists()) {
                return new Pair<>(ValidationResult.VALID_URL, site.name());
            } else {
                return new Pair<>(ValidationResult.NON_EXISTENT_URL, null);
            }
        }
    }

    @Override
    protected void onPostExecute(Pair<ValidationResult, String> validationResult) {
        super.onPostExecute(validationResult);
        URLValidatorTaskListener urlValidatorTaskListener = urlValidatorTaskListenerWeakReference.get();
        if (urlValidatorTaskListener != null) {
            urlValidatorTaskListener.handleValidatorResult(validationResult.first, url, validationResult.second);
        }
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

    private SongsFactory getSongsFactory() {
        if (songsFactory == null) {
            songsFactory = SongsFactory.getInstance();
        }
        return songsFactory;
    }
}
