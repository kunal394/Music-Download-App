package com.ks.musicdownloader.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.ks.musicdownloader.ArtistInfo;
import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.DownloadCallback;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.SongInfo;
import com.ks.musicdownloader.Utils.NetworkUtils;
import com.ks.musicdownloader.service.DownloaderService;
import com.ks.musicdownloader.songsprocessors.MusicSite;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("DanglingJavadoc")
public class ListSongsActivity extends AppCompatActivity {

    private static final String TAG = ListSongsActivity.class.getSimpleName();

    private static final int OTHER_FRAGMENTS = 2;
    private static final int NO_FRAGMENT = 0;
    private static int CURRENTLY_SELECTED_FRAGMENT = NO_FRAGMENT;

    private DrawerLayout drawerLayout;
    private ActionBar actionBar;
    private NavigationView navigationView;
    private FloatingActionButton downloadButton;

    private ArtistInfo parsedArtistInfo;
    MusicSite musicSite;
    private boolean downloading;
    private DownloaderService downloaderService;
    private ServiceConnection downloaderServiceConnection;
    private boolean networkConnected = false;
    private ConnectivityManager.NetworkCallback networkCallback;
    private DownloadCallback<Integer> downloadCallbackForDownloader;

    public void downloadSongs(View view) {
        changeDownloadButtonVisibility(View.GONE);
        downloading = true;
        parsedArtistInfo = getSelectedSongs();
        bindToDownloaderService();
    }

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

        // get the download button
        downloadButton = findViewById(R.id.download_button);
        changeDownloadButtonVisibility(View.VISIBLE);

        // parse intent extras
        getIntentExtras();

        createArtistMenuGroup();
        createAlbumMenuGroup();
        displayArtistFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (downloading) {
            changeDownloadButtonVisibility(View.GONE);
        }
        drawerLayout.addDrawerListener(createDrawerLayoutListener());
        navigationView.setNavigationItemSelectedListener(createNavigationViewListener());
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void init() {
        createNetworkCallback();
        NetworkUtils.regReceiverForConnectionValidationOnly(this, networkCallback);
        createServiceConnForDownloaderService();
        createDownloadCallbackForDownloader();
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        parsedArtistInfo = intent.getParcelableExtra(Constants.PARSED_ARTIST_INFO);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
        musicSite = Enum.valueOf(MusicSite.class, siteName);
    }

    private void createArtistMenuGroup() {
        Menu menu = navigationView.getMenu();
        SubMenu subMenu = menu.addSubMenu(R.string.artist_group_id, Menu.NONE, 100, R.string.artist_info);
        subMenu.add(R.string.artist_group_id, Menu.NONE, 100, parsedArtistInfo.getArtist());
        subMenu.setGroupCheckable(R.string.artist_group_id, true, true);
    }

    private void createAlbumMenuGroup() {
        Menu menu = navigationView.getMenu();
        SubMenu subMenu = menu.addSubMenu(R.string.album_group_id, Menu.NONE, 105, R.string.album_info);
        for (String album : parsedArtistInfo.getAlbumInfo().keySet()) {
            subMenu.add(R.string.album_group_id, Menu.NONE, 105, album);
        }
        subMenu.setGroupCheckable(R.string.album_group_id, true, true);
    }

    private void displayArtistFragment() {
        setActionBarTitle(R.string.artist_info);
        // TODO: 26-09-2018 fragment transaction
    }

    private void displayAlbumFragment(String album, HashMap<String, List<Integer>> albumInfo) {
        actionBar.setTitle(R.string.album_info);
        // TODO: 26-09-2018 fragment transaction
    }

    private void changeDownloadButtonVisibility(int vis) {
        if (downloadButton == null) {
            return;
        }
        if (vis == View.GONE) {
            downloadButton.hide();
        } else {
            downloadButton.show();
        }
    }

    private void setActionBarTitle(int stringRes) {
        if (actionBar != null) {
            actionBar.setTitle(stringRes);
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

    public ArtistInfo getSelectedSongs() {
        return parsedArtistInfo;
    }

    public ArtistInfo getParsedArtistInfo() {
        return parsedArtistInfo;
    }

    public void setParsedArtistInfo(ArtistInfo parsedArtistInfo) {
        this.parsedArtistInfo = parsedArtistInfo;
    }

    private void bindToDownloaderService() {
        Intent intent = new Intent(ListSongsActivity.this, DownloaderService.class);
        bindService(intent, downloaderServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void notifyDownloadedSong(Integer downloadedSongId) {
        // TODO: 12-09-2018 complete this
        SongInfo downloadedSongInfo = parsedArtistInfo.getSongsMap().get(downloadedSongId);
    }

    /******************Listeners************************************/
    /*********************And************************************/
    /******************Callbacks************************************/

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

                int groupId = menuItem.getGroupId();
                if (groupId == R.id.nav_settings_and_source) {
                    int itemId = menuItem.getItemId();
                    if (R.id.nav_source == itemId) {
                        displayAboutUsFragment();
                    } else if (R.id.nav_settings == itemId) {
                        displaySettingsFragment();
                    }
                } else if (groupId == R.string.artist_group_id) {
                    Log.d(TAG, "OnNavigationItemSelectedListener(): Clicked on artist!");
                    displayArtistFragment();
                } else if (groupId == R.string.album_group_id) {
                    checkForAlbumClick(menuItem.getTitle().toString());
                }
                return true;
            }

            private void checkForAlbumClick(String menuItemTitle) {
                for (String album : parsedArtistInfo.getAlbumInfo().keySet()) {
                    if (album.equals(menuItemTitle)) {
                        Log.d(TAG, "OnNavigationItemSelectedListener(): Clicked on album: " + album);
                        displayAlbumFragment(album, parsedArtistInfo.getAlbumInfo());
                    }
                }
            }
        };
    }

    private void createNetworkCallback() {
        Log.d(TAG, "createNetworkCallback()");
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                networkConnected = true;
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                networkConnected = false;
            }
        };
    }

    private void createServiceConnForDownloaderService() {
        Log.d(TAG, "createServiceConnForDownloaderService()");
        downloaderServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(TAG, "createServiceConnForDownloaderService() onServiceConnected");
                DownloaderService.LocalBinder binder = (DownloaderService.LocalBinder) iBinder;
                downloaderService = binder.getService();
                downloaderService.setDownloadCallback(downloadCallbackForDownloader);
                downloaderService.setMusicSite(musicSite);
                downloaderService.startDownload(parsedArtistInfo);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG, "createServiceConnForDownloaderService() onServiceDisconnected");
            }
        };
    }

    private void createDownloadCallbackForDownloader() {
        Log.d(TAG, "createDownloadCallbackForDownloader()");
        downloadCallbackForDownloader = new DownloadCallback<Integer>() {
            @Override
            public void updateFromDownload(Integer downloadedSongId) {
                notifyDownloadedSong(downloadedSongId);
            }

            @Override
            public void onProgressUpdate(int progressCode, int percentComplete) {
                switch (progressCode) {
                    case Progress.ERROR:
                        break;
                    case Progress.CONNECT_SUCCESS:
                        break;
                    case Progress.GET_INPUT_STREAM_SUCCESS:
                        break;
                    case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                        break;
                    case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                        break;
                }
            }

            @Override
            public void finishDownloading() {
            }
        };
    }
}
