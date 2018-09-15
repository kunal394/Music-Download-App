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
        if (intent == null) {
            Log.d(TAG, "Intent null!");
            sendBroadcast(createIntentForNullIntentReceived());
            return;
        }

        String url = intent.getStringExtra(Constants.DOWNLOAD_URL);
        MusicSite musicSite = (MusicSite) intent.getSerializableExtra(Constants.MUSIC_SITE);
        Log.d(TAG, "Parsing songs for music site: " + musicSite.name() + " with url: " + url);
        ArtistInfo artistInfo = null;
        String error = Constants.EMPTY_STRING;
        try {
            artistInfo = musicSite.getMusicParser(url).parseArtistInfo();
        } catch (IOException e) {
            Log.d(TAG, "Error found while parsing songs. Error: " + e);
            error = e.getMessage();
            e.printStackTrace();
        }
        if (artistInfo == Constants.DUMMY_ARTIST_INFO) {
            sendBroadcast(createParseErrorIntent(error));
        } else {
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
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PARSE_SUCCESS_MESSAGE_KEY, artistInfo);
        intent.putExtras(bundle);
        return intent;
    }
}
