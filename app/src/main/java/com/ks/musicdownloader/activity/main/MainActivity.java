package com.ks.musicdownloader.activity.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.ToastUtils;
import com.ks.musicdownloader.activity.DrawerActivityWithFragment;
import com.ks.musicdownloader.activity.common.AboutUsFragment;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.common.SettingsFragment;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends DrawerActivityWithFragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SEARCH_FRAGMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkForPermissions();
        Log.d(TAG, "onCreate() starts");
        super.onCreate(savedInstanceState);
        // inflate the global menu items inside the nav view
        navigationView.inflateMenu(R.menu.main_activity_menu_items);
        navigationView.setNavigationItemSelectedListener(createNavigationViewListener());
        displaySearchFragment();
        markSearchMenuItemChecked();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                displaySearchFragment();
                markSearchMenuItemChecked();
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
                    ToastUtils.displayLongToast(this, Constants.NO_EXTERNAL_STORAGE_PERMISSION_MESSAGE);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CURRENTLY_SELECTED_FRAGMENT == SEARCH_FRAGMENT) {
            CURRENTLY_SELECTED_FRAGMENT = Constants.NO_FRAGMENT;
        }
    }

    @Override
    public void onBackPressed() {
        if (CURRENTLY_SELECTED_FRAGMENT != SEARCH_FRAGMENT && CURRENTLY_SELECTED_FRAGMENT != Constants.NO_FRAGMENT) {
            displaySearchFragment();
            markSearchMenuItemChecked();
        } else {
            super.onBackPressed();
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void checkForPermissions() {
        ActivityCompat.requestPermissions(this, Constants.REQUIRED_PERMISSIONS, Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void markSearchMenuItemChecked() {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_search).setChecked(true);
    }

    private void displaySearchFragment() {
        if (CURRENTLY_SELECTED_FRAGMENT != SEARCH_FRAGMENT) {
            displayFragment(new SearchFragment(), R.string.action_search, SEARCH_FRAGMENT);
        }
    }

    private void displaySettingsFragment() {
        displayFragment(new SettingsFragment(), R.string.nav_settings, Constants.OTHER_FRAGMENTS);
    }

    private void displayAboutUsFragment() {
        displayFragment(new AboutUsFragment(), R.string.nav_source, Constants.OTHER_FRAGMENTS);
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
                        markSearchMenuItemChecked();
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
