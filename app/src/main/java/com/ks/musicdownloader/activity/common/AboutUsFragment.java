package com.ks.musicdownloader.activity.common;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.LogUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("DanglingJavadoc")
public class AboutUsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AboutUsFragment.class.getSimpleName();

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_about_us, container, false);
        ImageButton githubImageButton = fragment.findViewById(R.id.github_image_button);
        githubImageButton.setOnClickListener(this);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.github_image_button:
                redirectToGithubRepo();
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void redirectToGithubRepo() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GITHUB_REPO_URI));
        startActivity(browserIntent);
    }
}
