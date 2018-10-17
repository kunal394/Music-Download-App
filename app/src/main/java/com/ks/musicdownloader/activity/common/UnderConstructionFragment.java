package com.ks.musicdownloader.activity.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.LogUtils;

/**
 * Created by Kunal Singh(knl.singh) on 17-10-2018.
 */
public class UnderConstructionFragment extends Fragment {

    private static final String TAG = UnderConstructionFragment.class.getSimpleName();

    public UnderConstructionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }
}
