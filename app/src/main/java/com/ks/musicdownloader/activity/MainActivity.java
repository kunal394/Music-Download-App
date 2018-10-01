package com.ks.musicdownloader.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ks.musicdownloader.common.Constants;
import com.ks.musicdownloader.R;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SEARCH_FRAGMENT = 1;
    private static final int OTHER_FRAGMENTS = 2;
    private static final int NO_FRAGMENT = 0;
    private static int CURRENTLY_SELECTED_FRAGMENT = NO_FRAGMENT;

    private DrawerLayout drawerLayout;
    private ActionBar actionBar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() starts");
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
        // inflate the global menu items inside the nav view
        navigationView.inflateMenu(R.menu.main_activity_menu_items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        // inflate the top right search icon
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_top_bar_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        checkForPermissions();
        super.onStart();
        navigationView.setNavigationItemSelectedListener(createNavigationViewListener());
        markSearchMenuItemChecked();
        displaySearchFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                displaySearchFragment();
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ValidationResult.NO_EXTERNAL_STORAGE_PERMISSION.displayToast(this);
                    finish();
                }
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void checkForPermissions() {
        ActivityCompat.requestPermissions(this, Constants.REQUIRED_PERMISSIONS, Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void setActionBarTitle(int stringRes) {
        if (actionBar != null) {
            actionBar.setTitle(stringRes);
        }
    }

    private void markSearchMenuItemChecked() {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_search).setChecked(true);
    }

    private void displaySearchFragment() {
        if (CURRENTLY_SELECTED_FRAGMENT != SEARCH_FRAGMENT) {
            CURRENTLY_SELECTED_FRAGMENT = SEARCH_FRAGMENT;
            setActionBarTitle(R.string.action_search);
            displayFragment(new SearchFragment());
        }
    }

    private void displaySettingsFragment() {
        CURRENTLY_SELECTED_FRAGMENT = OTHER_FRAGMENTS;
        setActionBarTitle(R.string.nav_settings);
        displayFragment(new SettingsFragment());
    }

    private void displayAboutUsFragment() {
        CURRENTLY_SELECTED_FRAGMENT = OTHER_FRAGMENTS;
        setActionBarTitle(R.string.nav_source);
        displayFragment(new AboutUsFragment());
    }

    private void displayFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    /******************Listeners************************************/
    /*********************And************************************/
    /******************Callbacks************************************/

    @NonNull
    private NavigationView.OnNavigationItemSelectedListener createNavigationViewListener() {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);

                // close drawer when item is tapped
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_search:
                        displaySearchFragment();
                        break;
                    case R.id.nav_settings:
                        displaySettingsFragment();
                        break;
                    case R.id.nav_source:
                        displayAboutUsFragment();
                        break;
                }
                return true;
            }
        };
    }
}
