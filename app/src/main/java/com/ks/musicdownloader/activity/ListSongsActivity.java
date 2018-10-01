package com.ks.musicdownloader.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.util.Log;
import android.view.MenuItem;

import com.ks.musicdownloader.common.ArtistInfo;
import com.ks.musicdownloader.common.Constants;
import com.ks.musicdownloader.common.DownloadCallback;
import com.ks.musicdownloader.R;
import com.ks.musicdownloader.common.SongInfo;
import com.ks.musicdownloader.service.DownloaderService;
import com.ks.musicdownloader.songsprocessors.MusicSite;

@SuppressWarnings("DanglingJavadoc")
public class ListSongsActivity extends AppCompatActivity implements FragmentCallback {

    private static final String TAG = ListSongsActivity.class.getSimpleName();

    private static final int ARTIST_FRAGMENT = 1;
    private static int CURRENTLY_SELECTED_FRAGMENT = Constants.NO_FRAGMENT;

    private DrawerLayout drawerLayout;
    private ActionBar actionBar;
    private NavigationView navigationView;

    private ArtistInfo parsedArtistInfo;
    MusicSite musicSite;

    private boolean downloading;
    private DownloaderService downloaderService;
    private ServiceConnection downloaderServiceConnection;
    private DownloadCallback<Integer> downloadCallbackForDownloader;
    private Handler handler;

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

        // parse intent extras
        getIntentExtras();

        musicSite.createNavMenu(navigationView, parsedArtistInfo);
        createHandler();
        displayArtistFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        if (CURRENTLY_SELECTED_FRAGMENT != ARTIST_FRAGMENT) {
            handler.sendEmptyMessage(Constants.DISPLAY_OTHER_FRAGMENTS);
        }
        navigationView.setNavigationItemSelectedListener(createNavigationViewListener());
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
    protected void onStop() {
        super.onStop();
        deInit();
    }

    @Override
    public void download() {
        if (downloading) {
            return;
        }
        downloading = true;
//        bindToDownloaderService();
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void init() {
        createServiceConnForDownloaderService();
        createDownloadCallbackForDownloader();
    }

    private void deInit() {
        downloaderServiceConnection = null;
        downloadCallbackForDownloader = null;
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        parsedArtistInfo = intent.getParcelableExtra(Constants.PARSED_ARTIST_INFO);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
        musicSite = Enum.valueOf(MusicSite.class, siteName);
    }

    private void displayArtistFragment() {
        handler.sendEmptyMessage(Constants.DISPLAY_ARTIST_FRAGMENT);
        setActionBarTitle(R.string.artist_info);
        ArtistFragment artistFragment = new ArtistFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, parsedArtistInfo);
        artistFragment.setArguments(bundle);
        displayFragment(artistFragment);
    }

    private void displayAlbumFragment(String album) {
        handler.sendEmptyMessage(Constants.DISPLAY_OTHER_FRAGMENTS);
        setActionBarTitle(R.string.album_info);
        AlbumFragment albumFragment = new AlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, parsedArtistInfo);
        bundle.putString(Constants.ALBUM_TO_VIEW, album);
        albumFragment.setArguments(bundle);
        displayFragment(albumFragment);
    }

    private void displaySettingsFragment() {
        CURRENTLY_SELECTED_FRAGMENT = Constants.OTHER_FRAGMENTS;
        setActionBarTitle(R.string.nav_settings);
        displayFragment(new SettingsFragment());
    }

    private void displayAboutUsFragment() {
        CURRENTLY_SELECTED_FRAGMENT = Constants.OTHER_FRAGMENTS;
        setActionBarTitle(R.string.nav_source);
        displayFragment(new AboutUsFragment());
    }

    private void displayFragment(Fragment fragment) {
        logCheckedItems();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void logCheckedItems() {
        Integer size = parsedArtistInfo.getSongsMap().size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (parsedArtistInfo.getSongsMap().valueAt(i).isChecked()) {
                count++;
            }
        }
        Log.d(TAG, "logCheckedItems(): Check count: " + count);
    }

    private void setActionBarTitle(int stringRes) {
        if (actionBar != null) {
            actionBar.setTitle(stringRes);
        }
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
                        displayAlbumFragment(album);
                    }
                }
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

    public void createHandler() {
        // TODO: 27-09-2018 decide on its existence :P
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case Constants.DISPLAY_ARTIST_FRAGMENT:
                        break;
                    case Constants.DISPLAY_OTHER_FRAGMENTS:
                        break;
                }
            }
        };
    }
}
