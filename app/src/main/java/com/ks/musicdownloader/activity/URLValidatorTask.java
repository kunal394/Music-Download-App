package com.ks.musicdownloader.activity;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

public class URLValidatorTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<LinearLayout> progressBarLayoutReference;
    private String url;

    public URLValidatorTask(String url, LinearLayout progressBarLayout) {
        this.progressBarLayoutReference = new WeakReference<>(progressBarLayout);
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        setProgressBarVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        setProgressBarVisibility(View.GONE);
    }

    private void setProgressBarVisibility(int visible) {
        LinearLayout progressBarLayout = progressBarLayoutReference.get();
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(visible);
        }
    }
}
