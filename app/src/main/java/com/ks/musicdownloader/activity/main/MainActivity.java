package com.ks.musicdownloader.activity.main;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.LogUtils;
import com.ks.musicdownloader.Utils.ToastUtils;
import com.ks.musicdownloader.activity.DrawerActivityWithFragment;
import com.ks.musicdownloader.activity.common.Constants;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends DrawerActivityWithFragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SEARCH_FRAGMENT = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LogUtils.d(TAG, "onCreateOptionsMenu()");
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

    protected boolean backPressed() {
        if (CURRENTLY_SELECTED_FRAGMENT != SEARCH_FRAGMENT && CURRENTLY_SELECTED_FRAGMENT != Constants.NO_FRAGMENT) {
            displaySearchFragment();
            return true;
        }
        return false;
    }

    @Override
    protected void inflateActivitySpecificMenu() {
        navigationView.inflateMenu(R.menu.main_activity_menu_items);
    }

    @Override
    protected void displayInitialFragment() {
        displaySearchFragment();
    }

    @Override
    protected void init() {
        checkForPermissions();
    }

    @Override
    protected void checkForActivityRelatedMenuItems(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_search:
                displaySearchFragment();
                break;
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void checkForPermissions() {
        ActivityCompat.requestPermissions(this, Constants.REQUIRED_PERMISSIONS, Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void displaySearchFragment() {
        if (CURRENTLY_SELECTED_FRAGMENT != SEARCH_FRAGMENT) {
            displayFragment(new SearchFragment(), R.string.action_search, SEARCH_FRAGMENT);
            markMenuItemChecked(R.id.nav_search, true);
        }
    }
}
