package com.ks.musicdownloader.activity.main;


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
import com.ks.musicdownloader.Utils.ToastUtils;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.listsongs.ListSongsActivity;
import com.ks.musicdownloader.songsprocessors.MusicSite;

import java.util.Objects;

@SuppressWarnings("DanglingJavadoc")
public class SearchFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private View fragmentView;
    private RelativeLayout validatorProgressBarLayout;
    private TextView progressBarTextView;
    private TextView lastSearchTextView;
    private TextView lastSearchTextTitleView;
    private EditText searchEditText;

    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean networkConnected;
    private BroadcastReceiver broadcastReceiver;
    private Handler handler;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_search, container, false);

        validatorProgressBarLayout = fragmentView.findViewById(R.id.urlValidatorProgressBar);
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

    // use any getActivity() calls only after this function(onActivityCreated()) has started executing
    // this is called after onActivityCreated()
    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        init();

        // last search view handling
        String lastFetchedUrl = CommonUtils.getPrefString(Objects.requireNonNull(getContext()), Constants.SEARCH_PREF_NAME,
                Constants.PREF_LAST_FETCHED_URL_KEY, Constants.EMPTY_STRING);

        if (Constants.EMPTY_STRING.equals(lastFetchedUrl)) {
            handler.sendEmptyMessage(Constants.HIDE_LAST_SEARCH_VIEW);
        } else {
            handler.sendEmptyMessage(Constants.DISPLAY_LAST_SEARCH_VIEW);
            lastSearchTextView.setOnClickListener(this);
            lastSearchTextView.setText(lastFetchedUrl);
        }

        // progress bar handling
        Integer parsingStatus = CommonUtils.getPrefInt(getContext(), Constants.SEARCH_PREF_NAME,
                Constants.PREF_PARSING_STATUS_KEY, Constants.PARSING_COMPLETE);
        handler.sendEmptyMessage(parsingStatus);
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
        NetworkUtils.unRegReceiverForConnectionValidationOnly(getContext(), networkCallback);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick()");
        if (view == null) {
            Log.d(TAG, "onClick() view null!");
            return;
        }
        switch (view.getId()) {
            case R.id.fetch_songs_button:
                Log.d(TAG, "onclick() Extact button clicked!!");
                Integer parsingStatus = CommonUtils.getPrefInt(Objects.requireNonNull(getContext()), Constants.SEARCH_PREF_NAME,
                        Constants.PREF_PARSING_STATUS_KEY, Constants.PARSING_COMPLETE);
                if (parsingStatus == Constants.PARSING_COMPLETE) {
                    extractSongsFromURL();
                } else {
                    ToastUtils.displayLongToast(getContext(), Constants.PARSING_IN_PROGRESS);
                }
                break;
            case R.id.test_button:
                Log.d(TAG, "onclick() test button clicked!");
                test();
                break;

            case R.id.last_search_view:
                Log.d(TAG, "onclick() Last Search Text clicked!");
                searchEditText.setText(lastSearchTextView.getText());
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void init() {
        Log.d(TAG, "init()");
        networkConnected = false;
        createNetworkCallback();
        createHandler();
        registerBroadcastReceiver();
        NetworkUtils.regReceiverForConnectionValidationOnly(getContext(), networkCallback);
    }

    private void extractSongsFromURL() {
        Log.d(TAG, "extractSongsFromURL()");
        CommonUtils.hideKeyboard(Objects.requireNonNull(getActivity()));
        if (networkConnected) {
            validateURL();
        } else {
            ToastUtils.displayLongToast(getContext(), ValidationResult.NO_INTERNET.getMessage());
        }
    }

    private void validateURL() {
        Log.d(TAG, "validateURL()");
        updateParserStatusInPref(Constants.VALIDATING_PROGRESS);
        EditText editText = fragmentView.findViewById(R.id.search_url_editText);
        String url = editText.getText().toString();
        Intent intent = new Intent(getActivity(), URLValidatorService.class);
        intent.putExtra(Constants.DOWNLOAD_URL, url);
        Objects.requireNonNull(getActivity()).startService(intent);
    }

    private void updateParserStatusInPref(int parsingStatus) {
        CommonUtils.putPrefInt(Objects.requireNonNull(getContext()), Constants.SEARCH_PREF_NAME,
                Constants.PREF_PARSING_STATUS_KEY, parsingStatus);
        handler.sendEmptyMessage(parsingStatus);
    }

    private void createIntentAndDelegateActivity(ArtistInfo artistInfo, String siteName) {
        Log.d(TAG, "createIntentAndDelegateActivity()");
        Intent intent = new Intent(getContext(), ListSongsActivity.class);
        intent.putExtra(Constants.MUSIC_SITE, siteName);
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
                        validatorProgressBarLayout.setVisibility(View.VISIBLE);
                        break;
                    case Constants.PARSING_PROGRESS:
                        progressBarTextView.setText(R.string.parsingProgressText);
                        validatorProgressBarLayout.setVisibility(View.VISIBLE);
                        break;
                    case Constants.PARSING_COMPLETE:
                        validatorProgressBarLayout.setVisibility(View.GONE);
                        break;
                    case Constants.PARSE_ERROR:
                        validatorProgressBarLayout.setVisibility(View.GONE);
                        ToastUtils.displayLongToast(getContext(), ValidationResult.PARSING_ERROR.getMessage());
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
        intentFilter.addAction(Constants.VALIDATE_ERROR_ACTION_KEY);
        intentFilter.addAction(Constants.VALIDATE_SUCCESS_ACTION_KEY);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        Objects.requireNonNull(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    private class ParserBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "ParserBroadcastReceiver, onReceive()");
            String parseResult = intent.getAction();
            if (parseResult == null || parseResult.equals(Constants.EMPTY_STRING)) {
                return;
            }
            String errorMsg;
            switch (parseResult) {
                case Constants.VALIDATE_ERROR_ACTION_KEY:
                    handler.sendEmptyMessage(Constants.PARSING_COMPLETE);
                    errorMsg = intent.getStringExtra(Constants.VALIDATE_ERROR_MESSAGE_KEY);
                    ToastUtils.displayLongToast(getContext(), errorMsg);
                    Log.d(TAG, "ParserBroadcastReceiver, onReceive() VALIDATE_ERROR_ACTION_KEY, validate error: " + errorMsg);
                    break;
                case Constants.VALIDATE_SUCCESS_ACTION_KEY:
                    handler.sendEmptyMessage(Constants.PARSING_PROGRESS);
                    break;
                case Constants.PARSE_ERROR_ACTION_KEY:
                    handler.sendEmptyMessage(Constants.PARSE_ERROR);
                    errorMsg = intent.getStringExtra(Constants.PARSE_ERROR_MESSAGE_KEY);
                    Log.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_ERROR_ACTION_KEY, parse error: " + errorMsg);
                    break;
                case Constants.PARSE_SUCCESS_ACTION_KEY:
                    handler.sendEmptyMessage(Constants.PARSING_COMPLETE);
                    ArtistInfo artistInfo = intent.getParcelableExtra(Constants.PARSE_SUCCESS_MESSAGE_KEY);
                    String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
                    Log.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_SUCCESS_ACTION_KEY, artistInfo: "
                            + artistInfo.toString());
                    createIntentAndDelegateActivity(artistInfo, siteName);
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
