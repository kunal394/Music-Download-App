package com.ks.musicdownloader.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.Utils.TestUtils;
import com.ks.musicdownloader.common.ArtistInfo;
import com.ks.musicdownloader.common.Constants;
import com.ks.musicdownloader.service.ParserService;
import com.ks.musicdownloader.songsprocessors.MusicSite;

import java.util.Objects;

@SuppressWarnings("DanglingJavadoc")
public class SearchFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private View fragmentView;
    private RelativeLayout validatorProgressBar;
    private TextView progressBarTextView;
    private TextView lastSearchTextView;
    private TextView lastSearchTextTitleView;
    private EditText searchEditText;

    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean networkConnected;
    private URLValidatorTaskListener urlValidatorTaskListener;
    private BroadcastReceiver broadcastReceiver;
    private Handler handler;

    private String musicSite;
    private boolean parsing;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_search, container, false);

        validatorProgressBar = fragmentView.findViewById(R.id.urlValidatorProgressBar);
        progressBarTextView = fragmentView.findViewById(R.id.progressBarText);
        Button fetchSongsButton = fragmentView.findViewById(R.id.fetch_songs_button);
        fetchSongsButton.setOnClickListener(this);

        Button testButton = fragmentView.findViewById(R.id.test_button);
        testButton.setOnClickListener(this);

        lastSearchTextView = fragmentView.findViewById(R.id.last_search_view);
        lastSearchTextTitleView = fragmentView.findViewById(R.id.last_search_title_view);

        searchEditText = fragmentView.findViewById(R.id.search_url_editText);

        broadcastReceiver = new ParserBroadcastReceiver();
        return fragmentView;
    }

    // use any getActivity() calls only after this function has started executing
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        String lastFetchedUrl = CommonUtils.getPrefString(Objects.requireNonNull(getContext()), Constants.SEARCH_PREF_NAME,
                Constants.PREF_LAST_FETCHED_URL_KEY, Constants.EMPTY_STRING);
        if (Constants.EMPTY_STRING.equals(lastFetchedUrl)) {
            handler.sendEmptyMessage(Constants.HIDE_LAST_SEARCH_VIEW);
        } else {
            handler.sendEmptyMessage(Constants.DISPLAY_LAST_SEARCH_VIEW);
            lastSearchTextView.setOnClickListener(this);
            lastSearchTextView.setText(lastFetchedUrl);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Objects.requireNonNull(getActivity()).unregisterReceiver(broadcastReceiver);
        NetworkUtils.unRegReceiverForConnectionValidationOnly(getContext(), networkCallback);
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.fetch_songs_button:
                Log.d(TAG, "Extact button clicked!!");
                if (!parsing) {
                    parsing = true;
                    extractSongsFromURL();
                }
                break;
            case R.id.test_button:
                Log.d(TAG, "test button clicked!");
                test();
                break;

            case R.id.last_search_view:
                Log.d(TAG, "Last Search Text clicked!");
                searchEditText.setText(lastSearchTextView.getText());
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void init() {
        networkConnected = false;
        createNetworkCallback();
        createHandler();
        registerBroadcastReceiver();
        NetworkUtils.regReceiverForConnectionValidationOnly(getContext(), networkCallback);
        createUrlValidatorListener();
    }

    private void extractSongsFromURL() {
        Log.d(TAG, "extractSongsFromURL()");
        CommonUtils.hideKeyboard(Objects.requireNonNull(getActivity()));
        if (networkConnected) {
            validateURL();
        } else {
            displayErrorToast(ValidationResult.NO_INTERNET);
        }
    }

    private void validateURL() {
        // TODO: 26-09-2018 in my mobile the validator progress bar is hardly visible.
        // since the validation is pretty fast. need to think about it
        EditText editText = fragmentView.findViewById(R.id.search_url_editText);
        String url = editText.getText().toString();
        handler.sendEmptyMessage(Constants.VALIDATING_PROGRESS);
        new URLValidatorTask(url, urlValidatorTaskListener).execute();
    }

    private void displayErrorToast(ValidationResult validationResult) {
        validationResult.displayToast(getContext());
    }

    private void createIntentAndDelegateActivity(ArtistInfo artistInfo) {
        Intent intent = new Intent(getContext(), ListSongsActivity.class);
        intent.putExtra(Constants.MUSIC_SITE, musicSite);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, artistInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /******************Listeners************************************/
    /*********************And************************************/
    /******************Callbacks************************************/

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

    private void createHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage()");
                super.handleMessage(msg);
                switch (msg.what) {
                    case Constants.VALIDATING_PROGRESS:
                        progressBarTextView.setText(R.string.validatorProgressText);
                        validatorProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case Constants.PARSING_PROGRESS:
                        progressBarTextView.setText(R.string.parsingProgressText);
                        break;
                    case Constants.HIDE_PROGRESS_BAR:
                        validatorProgressBar.setVisibility(View.GONE);
                        break;
                    case Constants.PARSE_ERROR:
                        validatorProgressBar.setVisibility(View.GONE);
                        displayErrorToast(ValidationResult.PARSING_ERROR);
                        break;
                    case Constants.HIDE_LAST_SEARCH_VIEW:
                        lastSearchTextView.setVisibility(View.GONE);
                        lastSearchTextTitleView.setVisibility(View.GONE);
                        break;
                    case Constants.DISPLAY_LAST_SEARCH_VIEW:
                        lastSearchTextView.setVisibility(View.VISIBLE);
                        lastSearchTextTitleView.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.PARSE_ERROR_ACTION_KEY);
        intentFilter.addAction(Constants.PARSE_SUCCESS_ACTION_KEY);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        Objects.requireNonNull(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void createUrlValidatorListener() {
        urlValidatorTaskListener = new URLValidatorTaskListener() {
            @Override
            public void handleValidatorResult(ValidationResult validationResult, String url, String siteName) {
                if (validationResult.isValidResult()) {
                    handler.sendEmptyMessage(Constants.PARSING_PROGRESS);
                    musicSite = siteName;
                    Intent intent = new Intent(getActivity(), ParserService.class);
                    intent.putExtra(Constants.DOWNLOAD_URL, url);
                    intent.putExtra(Constants.MUSIC_SITE, siteName);
                    Objects.requireNonNull(getActivity()).startService(intent);
                } else {
                    handler.sendEmptyMessage(Constants.HIDE_PROGRESS_BAR);
                    displayErrorToast(validationResult);
                }
            }
        };
    }

    private class ParserBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "ParserBroadcastReceiver, onReceive()");
            String parseResult = intent.getAction();
            if (parseResult == null || parseResult.equals(Constants.EMPTY_STRING)) {
                return;
            }
            switch (parseResult) {
                case Constants.PARSE_ERROR_ACTION_KEY:
                    parsing = false;
                    String errorMsg = intent.getStringExtra(Constants.PARSE_ERROR_MESSAGE_KEY);
                    Log.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_ERROR_ACTION_KEY, parse error: " + errorMsg);
                    if (Constants.PARSE_ERROR_NULL_INTENT.equals(errorMsg)) {
                        Log.wtf(TAG, "ParserBroadcastReceiver, onReceive() parser service received null intent!");
                    } else if (Constants.PARSE_ERROR_NULL_ARTIST_INFO.equals(errorMsg)) {
                        Log.wtf(TAG, "ParserBroadcastReceiver, onReceive() parser service received null intent!");
                    }
                    handler.sendEmptyMessage(Constants.PARSE_ERROR);
                    break;
                case Constants.PARSE_SUCCESS_ACTION_KEY:
                    parsing = false;
                    ArtistInfo artistInfo = intent.getParcelableExtra(Constants.PARSE_SUCCESS_MESSAGE_KEY);
                    Log.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_SUCCESS_ACTION_KEY, artistInfo: "
                            + artistInfo.toString());
                    Log.d(TAG, "site: " + musicSite);
                    CommonUtils.putPrefString(Objects.requireNonNull(getContext()), Constants.SEARCH_PREF_NAME,
                            Constants.PREF_LAST_FETCHED_URL_KEY, artistInfo.getUrl());
                    handler.sendEmptyMessage(Constants.HIDE_PROGRESS_BAR);
                    createIntentAndDelegateActivity(artistInfo);
                    break;
                default:
                    break;
            }
        }
    }

    /******************Test************************************/
    /******************Code************************************/

    private void test() {
        Log.d(TAG, "test(): ");
        Boolean defaultChecked = CommonUtils.getPrefBoolean(Objects.requireNonNull(getContext()),
                Constants.SETTINGS_PREF_NAME, Constants.PREF_SELECT_ALL_KEY, false);
        Intent intent = new Intent(getContext(), ListSongsActivity.class);
        intent.putExtra(Constants.MUSIC_SITE, MusicSite.BANDCAMP.name());
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, TestUtils.createTestArtistInfo(defaultChecked));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
