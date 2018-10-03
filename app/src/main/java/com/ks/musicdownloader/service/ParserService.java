package com.ks.musicdownloader.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.common.ArtistInfo;
import com.ks.musicdownloader.common.Constants;
import com.ks.musicdownloader.songsprocessors.MusicSite;

import java.io.IOException;

public class ParserService extends IntentService {

    private static final String TAG = ParserService.class.getSimpleName();

    private String url;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ParserService(String name) {
        super(name);
    }

    public ParserService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent()");
        if (intent == null) {
            Log.d(TAG, "onHandleIntent(): Intent null!");
            sendBroadcast(createIntentForNullIntentReceived());
            return;
        }

        url = intent.getStringExtra(Constants.DOWNLOAD_URL);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
        ArtistInfo artistInfo = null;
        String error = Constants.EMPTY_STRING;
        Boolean defaultCheckedValue = getDefaultCheckedValue();
        try {
            MusicSite musicSite = MusicSite.valueOf(siteName);
            Log.d(TAG, "onHandleIntent(): Parsing songs for music site: " + musicSite.name() + " with url: " + url);
            artistInfo = musicSite.getMusicParser(url).parseArtistInfo(defaultCheckedValue);
        } catch (IOException e) {
            Log.d(TAG, "onHandleIntent(): Error found while parsing songs. Error: " + e);
            error = e.getMessage();
            e.printStackTrace();
        }
        if (artistInfo == Constants.DUMMY_ARTIST_INFO) {
            Log.d(TAG, "onHandleIntent(): Sending error broadcast with error: " + error);
            sendBroadcast(createParseErrorIntent(error));
        } else if (artistInfo == null) {
            Log.d(TAG, "onHandleIntent(): Sending error broadcast for null artist info!");
            sendBroadcast(createNullInfoIntent());
        } else {
            Log.d(TAG, "onHandleIntent(): Sending success broadcast.");
            artistInfo.initializeAlbumCheckedStatus(defaultCheckedValue);
            artistInfo.setUrl(url);
            sendBroadcast(createSuccessIntent(artistInfo));
        }
    }

    private Boolean getDefaultCheckedValue() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getBoolean(Constants.PREF_SELECT_ALL_KEY, false);
    }

    private Intent createIntentForNullIntentReceived() {
        Intent intent = new Intent();
        intent.setAction(Constants.PARSE_ERROR_ACTION_KEY);
        intent.putExtra(Constants.PARSE_ERROR_MESSAGE_KEY, Constants.PARSE_ERROR_NULL_INTENT);
        return intent;
    }

    private Intent createParseErrorIntent(String error) {
        Intent intent = new Intent();
        intent.setAction(Constants.PARSE_ERROR_ACTION_KEY);
        intent.putExtra(Constants.PARSE_ERROR_MESSAGE_KEY, error);
        return intent;
    }

    private Intent createNullInfoIntent() {
        Intent intent = new Intent();
        intent.setAction(Constants.PARSE_ERROR_ACTION_KEY);
        intent.putExtra(Constants.PARSE_ERROR_MESSAGE_KEY, Constants.PARSE_ERROR_NULL_ARTIST_INFO);
        return intent;
    }

    private Intent createSuccessIntent(ArtistInfo artistInfo) {
        Intent intent = new Intent();
        intent.setAction(Constants.PARSE_SUCCESS_ACTION_KEY);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSE_SUCCESS_MESSAGE_KEY, artistInfo);
        intent.putExtras(bundle);
        return intent;
    }
}
