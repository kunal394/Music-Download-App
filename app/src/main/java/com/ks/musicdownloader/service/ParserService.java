package com.ks.musicdownloader.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

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

    }
}
