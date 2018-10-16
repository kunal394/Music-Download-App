package com.ks.musicdownloader.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.Utils.PrefUtils;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.listsongs.ListSongsActivity;
import com.ks.musicdownloader.songsprocessors.MusicSite;

import java.io.IOException;

@SuppressWarnings("DanglingJavadoc")
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
            updateParsingPreference();
            sendBroadcast(createParseErrorIntent(Constants.PARSE_ERROR_NULL_INTENT));
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
        } finally {
            updateParsingPreference();
        }
        if (artistInfo == Constants.DUMMY_ARTIST_INFO) {
            Log.d(TAG, "onHandleIntent(): Sending error broadcast with error: " + error);
            sendBroadcast(createParseErrorIntent(error));
        } else if (artistInfo == null) {
            Log.d(TAG, "onHandleIntent(): Sending error broadcast for null artist info!");
            sendBroadcast(createParseErrorIntent(Constants.PARSE_ERROR_NULL_ARTIST_INFO));
        } else {
            artistInfo.initializeAlbumCheckedStatus(defaultCheckedValue);
            artistInfo.setUrl(url);
            if (CommonUtils.appInForeground(getApplicationContext())) {
                Log.d(TAG, "onHandleIntent(): Sending success broadcast.");
                PrefUtils.putPrefString(getApplicationContext(), Constants.SEARCH_PREF_NAME,
                        Constants.PREF_LAST_FETCHED_URL_KEY, artistInfo.getUrl());
                sendBroadcast(createParseSuccessIntent(siteName, artistInfo));
            } else {
                Log.d(TAG, "onHandleIntent(): Sending success notification.");
                sendSuccessNotification(siteName, artistInfo);
            }
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void sendSuccessNotification(String siteName, ArtistInfo artistInfo) {
        String body = "Parsing complete for the url: " + url;
        Intent notifyIntent = new Intent(getApplicationContext(), ListSongsActivity.class);
        notifyIntent.setAction(Intent.ACTION_VIEW);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, artistInfo);
        bundle.putString(Constants.MUSIC_SITE, siteName);
        bundle.putString(Constants.NOTI_ID_KEY, Constants.LIST_SONGS_NOTIFICATION_ID.toString());
        notifyIntent.putExtras(bundle);
        CommonUtils.sendNotification(getApplicationContext(), Constants.LIST_SONGS_NOTIFICATION_TITLE
                , body, Constants.LIST_SONGS_NOTIFICATION_CHANNEL_ID, notifyIntent,
                Constants.LIST_SONGS_NOTIFICATION_ID, R.drawable.ic_launcher_background);
    }

    private Intent createParseErrorIntent(String error) {
        Intent intent = new Intent();
        intent.setAction(Constants.PARSE_ERROR_ACTION_KEY);
        intent.putExtra(Constants.PARSE_ERROR_MESSAGE_KEY, error);
        return intent;
    }

    private Intent createParseSuccessIntent(String siteName, ArtistInfo artistInfo) {
        Intent intent = new Intent();
        intent.setAction(Constants.PARSE_SUCCESS_ACTION_KEY);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSE_SUCCESS_MESSAGE_KEY, artistInfo);
        bundle.putString(Constants.MUSIC_SITE, siteName);
        intent.putExtras(bundle);
        return intent;
    }

    private void updateParsingPreference() {
        PrefUtils.putPrefInt(getApplicationContext(), Constants.SEARCH_PREF_NAME,
                Constants.PREF_PARSING_STATUS_KEY, Constants.PARSING_COMPLETE);
    }

    private Boolean getDefaultCheckedValue() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getBoolean(Constants.PREF_SELECT_ALL_KEY, false);
    }
}
