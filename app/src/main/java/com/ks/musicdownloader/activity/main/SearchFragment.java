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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.Utils.LogUtils;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.Utils.PrefUtils;
import com.ks.musicdownloader.Utils.StringUtils;
import com.ks.musicdownloader.Utils.TestUtils;
import com.ks.musicdownloader.Utils.ToastUtils;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.listsongs.ListSongsActivity;
import com.ks.musicdownloader.service.URLValidatorService;
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
        LogUtils.d(TAG, "onCreateView()");
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
        LogUtils.d(TAG, "onStart()");
        super.onStart();
        init();

        // last search view handling
        String lastFetchedUrl = PrefUtils.getPrefString(Objects.requireNonNull(getContext()), Constants.SEARCH_PREF_NAME,
                Constants.PREF_LAST_FETCHED_URL_KEY, StringUtils.emptyString());

        if (StringUtils.isEmpty(lastFetchedUrl)) {
            handler.sendEmptyMessage(Constants.HIDE_LAST_SEARCH_VIEW);
        } else {
            handler.sendEmptyMessage(Constants.DISPLAY_LAST_SEARCH_VIEW);
            lastSearchTextView.setOnClickListener(this);
            lastSearchTextView.setText(lastFetchedUrl);
        }

        // progress bar handling
        Integer parsingStatus = PrefUtils.getPrefInt(getContext(), Constants.SEARCH_PREF_NAME,
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
        LogUtils.d(TAG, "onDestroyView()");
        super.onDestroyView();
        NetworkUtils.unRegReceiverForConnectionValidationOnly(getContext(), networkCallback);
    }

    @Override
    public void onClick(View view) {
        LogUtils.d(TAG, "onClick()");
        if (view == null) {
            LogUtils.d(TAG, "onClick() view null!");
            return;
        }
        switch (view.getId()) {
            case R.id.fetch_songs_button:
                LogUtils.d(TAG, "onclick() Extact button clicked!!");
                Integer parsingStatus = PrefUtils.getPrefInt(Objects.requireNonNull(getContext()), Constants.SEARCH_PREF_NAME,
                        Constants.PREF_PARSING_STATUS_KEY, Constants.PARSING_COMPLETE);
                CommonUtils.hideKeyboard(Objects.requireNonNull(getActivity()));
                if (!networkConnected) {
                    ToastUtils.displayLongToast(getContext(), Constants.NO_INTERNET_MESSAGE);
                } else if (parsingStatus != Constants.PARSING_COMPLETE) {
                    ToastUtils.displayLongToast(getContext(), Constants.PARSING_IN_PROGRESS);
                } else {
                    validateURL();
                }
                break;
            case R.id.test_button:
                LogUtils.d(TAG, "onclick() test button clicked!");
                test();
                break;

            case R.id.last_search_view:
                LogUtils.d(TAG, "onclick() Last Search Text clicked!");
                searchEditText.setText(lastSearchTextView.getText());
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void init() {
        LogUtils.d(TAG, "init()");
        networkConnected = false;
        createNetworkCallback();
        createHandler();
        registerBroadcastReceiver();
        NetworkUtils.regReceiverForConnectionValidationOnly(getContext(), networkCallback);
    }

    private void validateURL() {
        LogUtils.d(TAG, "validateURL()");
        updateParserStatusInPref();
        EditText editText = fragmentView.findViewById(R.id.search_url_editText);
        String url = editText.getText().toString();
        Intent intent = new Intent(getActivity(), URLValidatorService.class);
        intent.putExtra(Constants.DOWNLOAD_URL, url);
        Objects.requireNonNull(getActivity()).startService(intent);
    }

    private void updateParserStatusInPref() {
        PrefUtils.putPrefInt(Objects.requireNonNull(getContext()), Constants.SEARCH_PREF_NAME,
                Constants.PREF_PARSING_STATUS_KEY, Constants.VALIDATING_PROGRESS);
        handler.sendEmptyMessage(Constants.VALIDATING_PROGRESS);
    }

    private void createIntentAndDelegateActivity(ArtistInfo artistInfo, String siteName) {
        LogUtils.d(TAG, "createIntentAndDelegateActivity()");
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
        LogUtils.d(TAG, "createNetworkCallback()");
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
                LogUtils.d(TAG, "handleMessage()");
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
                        ToastUtils.displayLongToast(getContext(), Constants.PARSE_ERROR_MESSAGE);
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
            LogUtils.d(TAG, "ParserBroadcastReceiver, onReceive()");
            String parseResult = intent.getAction();
            if (StringUtils.isEmpty(parseResult)) {
                return;
            }
            String errorMsg;
            switch (parseResult) {
                case Constants.VALIDATE_ERROR_ACTION_KEY:
                    handler.sendEmptyMessage(Constants.PARSING_COMPLETE);
                    errorMsg = intent.getStringExtra(Constants.VALIDATE_ERROR_MESSAGE_KEY);
                    ToastUtils.displayLongToast(getContext(), errorMsg);
                    LogUtils.d(TAG, "ParserBroadcastReceiver, onReceive() VALIDATE_ERROR_ACTION_KEY, validate error: " + errorMsg);
                    break;
                case Constants.VALIDATE_SUCCESS_ACTION_KEY:
                    handler.sendEmptyMessage(Constants.PARSING_PROGRESS);
                    break;
                case Constants.PARSE_ERROR_ACTION_KEY:
                    handler.sendEmptyMessage(Constants.PARSE_ERROR);
                    errorMsg = intent.getStringExtra(Constants.PARSE_ERROR_MESSAGE_KEY);
                    LogUtils.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_ERROR_ACTION_KEY, parse error: " + errorMsg);
                    break;
                case Constants.PARSE_SUCCESS_ACTION_KEY:
                    handler.sendEmptyMessage(Constants.PARSING_COMPLETE);
                    ArtistInfo artistInfo = intent.getParcelableExtra(Constants.PARSE_SUCCESS_MESSAGE_KEY);
                    String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
                    LogUtils.d(TAG, "ParserBroadcastReceiver, onReceive() PARSE_SUCCESS_ACTION_KEY, artistInfo: "
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
        LogUtils.d(TAG, "test(): ");
        Boolean defaultChecked = PrefUtils.getPrefBoolean(Objects.requireNonNull(getContext()),
                Constants.SETTINGS_PREF_NAME, Constants.PREF_SELECT_ALL_KEY, false);
        Intent intent = new Intent(getContext(), ListSongsActivity.class);
        intent.putExtra(Constants.MUSIC_SITE, MusicSite.BANDCAMP.name());
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, TestUtils.createTestArtistInfo(defaultChecked));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
