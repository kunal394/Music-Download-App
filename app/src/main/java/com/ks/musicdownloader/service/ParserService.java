package com.ks.musicdownloader.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.songsprocessors.MusicSite;

import java.io.IOException;

public class ParserService extends IntentService {

    private static final String TAG = ParserService.class.getSimpleName();

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

        String url = intent.getStringExtra(Constants.DOWNLOAD_URL);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
        ArtistInfo artistInfo = null;
        String error = Constants.EMPTY_STRING;
        try {
            MusicSite musicSite = MusicSite.valueOf(siteName);
            Log.d(TAG, "onHandleIntent(): Parsing songs for music site: " + musicSite.name() + " with url: " + url);
            artistInfo = musicSite.getMusicParser(url).parseArtistInfo();
        } catch (IOException e) {
            Log.d(TAG, "onHandleIntent(): Error found while parsing songs. Error: " + e);
            error = e.getMessage();
            e.printStackTrace();
        }
        if (artistInfo == Constants.DUMMY_ARTIST_INFO) {
            Log.d(TAG, "onHandleIntent(): Sending error broadcast with error: " + error);
            sendBroadcast(createParseErrorIntent(error));
        } else {
            Log.d(TAG, "onHandleIntent(): Sending success broadcast.");
            sendBroadcast(createSuccessIntent(artistInfo));
        }
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

    private Intent createSuccessIntent(ArtistInfo artistInfo) {
        Intent intent = new Intent();
        intent.setAction(Constants.PARSE_SUCCESS_ACTION_KEY);
        // TODO: 16-09-2018 will need to make artistInfo parcealable, no workaround to it
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PARSE_SUCCESS_MESSAGE_KEY, artistInfo);
        intent.putExtras(bundle);
        return intent;
    }
}
