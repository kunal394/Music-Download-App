package com.ks.musicdownloader.activity;

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

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.SongInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("DanglingJavadoc")
public class AlbumFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AlbumFragment.class.getSimpleName();

    public RecyclerView recyclerView;
    private View fragmentView;
    private FloatingActionButton downloadButton;
    private LinearLayoutManager linearLayoutManager;

    // Any changes made on this object in this file will also reflect
    // in the parsedArtistInfo object in ListSongsActivity since they both
    // are same references.
    private ArtistInfo artistInfo;

    private AlbumAdapterCallback adapterCallback;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(@Nullable Bundle bundle) {
        super.setArguments(bundle);
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
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_artist, container, false);

        createAdapterCallback();

        // set adapter for recycler view
        recyclerView = fragmentView.findViewById(R.id.artist_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<SongInfo> songInfoList = artistInfo.getSongsList();
        recyclerView.setAdapter(new AlbumAdapter(songInfoList, adapterCallback));

        return fragmentView;
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        switch (view.getId()) {
            default:
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void removeFragment() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void createAdapterCallback() {
        adapterCallback = new AlbumAdapterCallback() {

            @Override
            public void setSongCheckedStatus(Integer songId, Boolean status) {
                artistInfo.setSongCheckedStatus(songId, status);
            }
        };
    }
}
