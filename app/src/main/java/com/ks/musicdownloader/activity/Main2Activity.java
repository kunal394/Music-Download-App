package com.ks.musicdownloader.activity;

import android.os.Bundle;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ks.musicdownloader.R;

@SuppressWarnings("DanglingJavadoc")
public class Main2Activity extends AppCompatActivity {

    public static final int SEARCH_FRAGMENT = 1;
    int CURRENTLY_SELECTED_FRAGMENT = 0;

    private DrawerLayout drawerLayout;
    ActionBar actionBar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setTitle(R.string.action_search);
        }
        navigationView = findViewById(R.id.nav_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displaySearchFragment();
        drawerLayout.addDrawerListener(createDrawerLayoutListener());
        navigationView.setNavigationItemSelectedListener(createNavigationViewListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.global, menu);
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

    /******************Private************************************/
    /******************Methods************************************/

    @NonNull
    private DrawerLayout.DrawerListener createDrawerLayoutListener() {
        return new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        };
    }

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
                        setActionBarTitle(R.string.action_search);
                        displaySearchFragment();
                        break;
                    case R.id.nav_settings:
                        setActionBarTitle(R.string.nav_settings);
                        displaySettingsFragment();
                        break;
                    case R.id.nav_source:
                        setActionBarTitle(R.string.nav_source);
                        displayAboutUsFragment();
                        break;
                }
                return true;
            }
        };
    }

    private void setActionBarTitle(int stringRes) {
        if (actionBar != null) {
            actionBar.setTitle(stringRes);
        }
    }

    private void displaySearchFragment() {
        if (CURRENTLY_SELECTED_FRAGMENT != SEARCH_FRAGMENT) {
            displayFragment(new SearchFragment());
        }
    }

    private void displaySettingsFragment() {
        CURRENTLY_SELECTED_FRAGMENT = 0;
        displayFragment(new SettingsFragment());
    }

    private void displayAboutUsFragment() {
        CURRENTLY_SELECTED_FRAGMENT = 0;
        displayFragment(new AboutUsFragment());
    }

    private void displayFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content_frame, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
}
