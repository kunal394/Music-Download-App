package com.ks.musicdownloader.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.activity.common.Constants;

/**
 * Created by Kunal Singh(knl.singh) on 16-10-2018.
 */
@SuppressWarnings("DanglingJavadoc")
public abstract class DrawerActivityWithFragment extends AppCompatActivity {

    private static final String TAG = DrawerActivityWithFragment.class.getSimpleName();

    protected int CURRENTLY_SELECTED_FRAGMENT = Constants.NO_FRAGMENT;

    protected DrawerLayout drawerLayout;
    protected ActionBar actionBar;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() starts");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        super.onCreate(savedInstanceState);

        // set activity layout
        setContentView(R.layout.activity_drawer);

        //get the drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);

        //set the top action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        // get the navigation view
        navigationView = findViewById(R.id.nav_view);
    }

    protected void displayFragment(Fragment fragment, int stringRes, int selectedFragment) {
        Log.d(TAG, "displayFragment: ");
        CURRENTLY_SELECTED_FRAGMENT = selectedFragment;
        setActionBarTitle(stringRes);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void setActionBarTitle(int stringRes) {
        Log.d(TAG, "setActionBarTitle: ");
        if (actionBar != null) {
            actionBar.setTitle(stringRes);
        }
    }
}
