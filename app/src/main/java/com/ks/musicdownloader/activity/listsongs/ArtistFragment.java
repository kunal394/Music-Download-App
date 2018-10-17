package com.ks.musicdownloader.activity.listsongs;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.LogUtils;
import com.ks.musicdownloader.Utils.StringUtils;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.common.FragmentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("DanglingJavadoc")
public class ArtistFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ArtistFragment.class.getSimpleName();

    private CheckedTextView selectAllCheckView;
    private ArtistAdapter adapter;

    // Any changes made on this object in this file will also reflect
    // in the parsedArtistInfo object in ListSongsActivity since they both
    // are same references.
    private ArtistInfo artistInfo;

    private FragmentCallback fragmentCallback;
    private ArtistAdapterCallback adapterCallback;

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(@Nullable Bundle bundle) {
        LogUtils.d(TAG, "setArguments");
        super.setArguments(bundle);
        if (bundle == null) {
            Log.wtf(TAG, "No artist info found! Killing fragment!!");
            removeFragment();
            return;
        }
        artistInfo = bundle.getParcelable(Constants.PARSED_ARTIST_INFO);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_artist, container, false);
        TextView titleView = fragmentView.findViewById(R.id.fragment_title);
        titleView.setText(artistInfo.getArtist());

        selectAllCheckView = fragmentView.findViewById(R.id.check_select_all);
        selectAllCheckView.setChecked(artistInfo.getArtistCheckedStatus());
        selectAllCheckView.setOnClickListener(this);
        selectAllCheckView.setText(getCheckAllText(artistInfo.getCheckedAlbumCount()));

        // set listener for download button
        FloatingActionButton downloadButton = fragmentView.findViewById(R.id.download_button);
        downloadButton.setOnClickListener(this);

        // set callbacks
        fragmentCallback = (FragmentCallback) getActivity();
        createAdapterCallback();

        // set adapter for recycler view
        RecyclerView recyclerView = fragmentView.findViewById(R.id.artist_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<String> albumNames = new ArrayList<>(artistInfo.getAlbumInfo().keySet());
        adapter = new ArtistAdapter(albumNames, artistInfo.getAlbumCheckedStatus(), adapterCallback);
        recyclerView.setAdapter(adapter);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume() ");
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.download_button:
                if (fragmentCallback != null) {
                    fragmentCallback.download();
                } else {
                    LogUtils.d(TAG, "onClick(): fragmentCallback null!");
                }
                break;
            case R.id.check_select_all:
                boolean newStatus = artistInfo.flipArtistCheckedStatus();
                selectAllCheckView.setChecked(newStatus);
                adapter.updateAlbumCheckedStatus(artistInfo.getAlbumCheckedStatus());
                adapter.notifyDataSetChanged();
                Integer checkedCount = 0;
                if (newStatus) {
                    checkedCount = artistInfo.getAlbumInfo().size();
                }
                selectAllCheckView.setText(getCheckAllText(checkedCount));
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void removeFragment() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private String getCheckAllText(Integer checkedCount) {
        return StringUtils.add(checkedCount.toString(), "/", artistInfo.getTotalAlbumCount().toString(), " selected");
    }

    /******************Listeners************************************/
    /*********************And************************************/
    /******************Callbacks************************************/

    private void createAdapterCallback() {
        adapterCallback = new ArtistAdapterCallback() {

            @Override
            public void setAlbumCheckedStatus(String album, Boolean status, Integer checkedCount) {
                artistInfo.setAlbumCheckedStatus(album, status);
                selectAllCheckView.setText(getCheckAllText(checkedCount));
                if (!status) {
                    selectAllCheckView.setChecked(false);
                }
            }

            @Override
            public void notifyAllChecked() {
                selectAllCheckView.setChecked(true);
                artistInfo.setArtistCheckedStatus(true);
            }
        };
    }
}
