package com.ks.musicdownloader.activity.listsongs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.ks.musicdownloader.activity.common.SongInfo;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("DanglingJavadoc")
public class AlbumFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AlbumFragment.class.getSimpleName();

    private CheckedTextView selectAllCheckView;
    private AlbumAdapter adapter;

    // Any changes made on this object in this file will also reflect
    // in the parsedArtistInfo object in ListSongsActivity since they both
    // are same references.
    private ArtistInfo artistInfo;
    private String album;

    private AlbumAdapterCallback adapterCallback;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(@Nullable Bundle bundle) {
        super.setArguments(bundle);
        if (bundle == null) {
            Log.wtf(TAG, "No artist info found! Killing fragment!!");
            removeFragment();
            return;
        }
        artistInfo = bundle.getParcelable(Constants.PARSED_ARTIST_INFO);
        album = bundle.getString(Constants.ALBUM_TO_VIEW);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_artist, container, false);
        TextView titleView = fragmentView.findViewById(R.id.fragment_title);
        titleView.setText(album);

        selectAllCheckView = fragmentView.findViewById(R.id.check_select_all);
        selectAllCheckView.setChecked(artistInfo.getAlbumCheckedStatus().get(album));
        selectAllCheckView.setOnClickListener(this);
        selectAllCheckView.setText(getCheckAllText(artistInfo.getCheckedSongsCountInAlbum(album)));

        // set adapter for recycler view
        createAdapterCallback();
        RecyclerView recyclerView = fragmentView.findViewById(R.id.artist_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<SongInfo> songInfoList = artistInfo.getSongsList(album);
        adapter = new AlbumAdapter(songInfoList, adapterCallback);
        recyclerView.setAdapter(adapter);

        return fragmentView;
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.check_select_all:
                boolean newStatus = artistInfo.flipAlbumCheckedStatus(album);
                selectAllCheckView.setChecked(newStatus);
                adapter.updateSongInfoList(artistInfo.getSongsList(album));
                adapter.notifyDataSetChanged();
                Integer checkedCount = 0;
                if (newStatus) {
                    checkedCount = artistInfo.getSongsList(album).size();
                }
                selectAllCheckView.setText(getCheckAllText(checkedCount));
            default:
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void removeFragment() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private String getCheckAllText(Integer checkedSongsCountInAlbum) {
        return StringUtils.add(checkedSongsCountInAlbum.toString(), "/",
                artistInfo.getSongsCountInAlbum(album).toString(), " selected");
    }

    /******************Listeners************************************/
    /*********************And************************************/
    /******************Callbacks************************************/

    private void createAdapterCallback() {
        adapterCallback = new AlbumAdapterCallback() {

            @Override
            public void setSongCheckedStatus(Integer songId, Boolean status, Integer checkedCount) {
                artistInfo.setSongCheckedStatus(album, songId, status);
                selectAllCheckView.setChecked(artistInfo.getAlbumCheckedStatus(album));
                selectAllCheckView.setText(getCheckAllText(checkedCount));
            }

            @Override
            public void notifyAllChecked() {
                selectAllCheckView.setChecked(true);
                artistInfo.setAlbumCheckedStatus(album, true);
            }
        };
    }
}
