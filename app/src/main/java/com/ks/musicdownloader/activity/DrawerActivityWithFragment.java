package com.ks.musicdownloader.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.LogUtils;
import com.ks.musicdownloader.activity.common.AboutUsFragment;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.common.SettingsFragment;
import com.ks.musicdownloader.activity.common.UnderConstructionFragment;

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
    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreate() starts");
        init();
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
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
        navigationView.setNavigationItemSelectedListener(createNavigationViewListener());

        inflateActivitySpecificMenu();
        createHandler();
        displayInitialFragment();
    }

    protected void displaySettingsFragment() {
        displayFragment(new SettingsFragment(), R.string.nav_settings_title, Constants.OTHER_FRAGMENTS);
    }

    protected void displayAboutUsFragment() {
        displayFragment(new AboutUsFragment(), R.string.nav_source_title, Constants.OTHER_FRAGMENTS);
    }

    protected void displayOngoingDownloadsFragment() {
        displayFragment(new UnderConstructionFragment(), R.string.nav_ongoing_downloads_title, Constants.OTHER_FRAGMENTS);
    }

    protected void displayCompletedDownloadsFragment() {
        displayFragment(new UnderConstructionFragment(), R.string.nav_completed_downloads_title, Constants.OTHER_FRAGMENTS);
    }

    protected void displayFragment(final Fragment fragment, final int stringRes, int selectedFragment) {
        LogUtils.d(TAG, "displayFragment: ");
        CURRENTLY_SELECTED_FRAGMENT = selectedFragment;
        Runnable fragmentTransactionRunnable = new Runnable() {
            @Override
            public void run() {
                setActionBarTitle(stringRes);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            }
        };
        handler.postDelayed(fragmentTransactionRunnable, Constants.FRAGMENT_TRANSACTION_DELAY);
    }

    protected void markMenuItemChecked(int resid, boolean checked) {
        Menu menu = navigationView.getMenu();
        menu.findItem(resid).setChecked(checked);
    }

    protected void performPreCheckOnNav() {
        // TODO: 17-10-2018 This is just a hack!!!! Do something about this
        // in list songs activity, the artist menu item remains checked even when other menu
        // items are selected. To remove the check we have to tap onto it once and then
        // select some other menu, then it probably gets added in the drawer layout stack and
        // from then on the drawer layout handles it and it works fine. But when the list songs
        // activity is initially opened unless we tap on the artist once, it remains selected
        // no matter how many times other menu items are selected.
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        // if the activity does not consumes the back press, then call super
        if (!backPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * can be used by the activities to handle their local messages
     */
    protected void handleLocalMessage() {
    }

    protected abstract void inflateActivitySpecificMenu();

    protected abstract void displayInitialFragment();

    protected abstract void init();

    protected abstract void checkForActivityRelatedMenuItems(MenuItem menuItem);

    protected abstract boolean backPressed();

    /******************Private************************************/
    /******************Methods************************************/

    private void setActionBarTitle(int stringRes) {
        LogUtils.d(TAG, "setActionBarTitle: ");
        if (actionBar != null) {
            actionBar.setTitle(stringRes);
        }
    }

    /******************Listeners************************************/
    /*********************And************************************/
    /******************Callbacks************************************/

    private void createHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                LogUtils.d(TAG, "handleMessage()");
                super.handleMessage(msg);
                switch (msg.what) {
                    default:
                        handleLocalMessage();
                        break;
                }
            }
        };
    }

    @NonNull
    protected NavigationView.OnNavigationItemSelectedListener createNavigationViewListener() {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                performPreCheckOnNav();
                // set item as selected to persist highlight
                menuItem.setChecked(true);

                // close drawer when item is tapped
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_completed_downloads:
                        displayCompletedDownloadsFragment();
                        break;
                    case R.id.nav_ongoing_downloads:
                        displayOngoingDownloadsFragment();
                        break;
                    case R.id.nav_settings:
                        displaySettingsFragment();
                        break;
                    case R.id.nav_source:
                        displayAboutUsFragment();
                        break;
                    default:
                        checkForActivityRelatedMenuItems(menuItem);
                }
                return true;
            }
        };
    }
}
