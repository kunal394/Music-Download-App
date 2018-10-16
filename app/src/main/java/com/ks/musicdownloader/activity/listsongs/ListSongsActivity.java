package com.ks.musicdownloader.activity.listsongs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.MenuItem;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.activity.DrawerActivityWithFragment;
import com.ks.musicdownloader.activity.common.AboutUsFragment;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.common.FragmentCallback;
import com.ks.musicdownloader.activity.common.SettingsFragment;
import com.ks.musicdownloader.service.DownloaderService;
import com.ks.musicdownloader.songsprocessors.MusicSite;

@SuppressWarnings("DanglingJavadoc")
public class ListSongsActivity extends DrawerActivityWithFragment implements FragmentCallback {

    private static final String TAG = ListSongsActivity.class.getSimpleName();

    private static final int ARTIST_FRAGMENT = 1;

    private ArtistInfo parsedArtistInfo;
    private MusicSite musicSite;

    private boolean downloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        // parse intent extras
        getIntentExtras();

        musicSite.createNavMenu(navigationView, parsedArtistInfo);
        displayArtistFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        navigationView.setNavigationItemSelectedListener(createNavigationViewListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
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
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (CURRENTLY_SELECTED_FRAGMENT != ARTIST_FRAGMENT) {
            displayArtistFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void download() {
        if (downloading) {
            return;
        }
        downloading = true;
//        startDownloaderService();
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void getIntentExtras() {
        Log.d(TAG, "getIntentExtras(): ");
        Intent intent = getIntent();
        parsedArtistInfo = intent.getParcelableExtra(Constants.PARSED_ARTIST_INFO);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
        Log.d(TAG, "getIntentExtras() sitename: " + siteName);
        musicSite = Enum.valueOf(MusicSite.class, siteName);
        Log.d(TAG, "getIntentExtras() artInfo: " + parsedArtistInfo.toString());
        if (intent.hasExtra(Constants.NOTI_ID_KEY)) {
            Log.d(TAG, "getIntentExtras()");
            CommonUtils.cancelNotification(this, Integer.valueOf(intent.getStringExtra(Constants.NOTI_ID_KEY)));
        }
    }

    private void startDownloaderService() {
        Log.d(TAG, "startDownloaderService: ");
        Intent intent = new Intent(getApplicationContext(), DownloaderService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSE_SUCCESS_MESSAGE_KEY, parsedArtistInfo);
        bundle.putString(Constants.MUSIC_SITE, musicSite.name());
        intent.putExtras(bundle);
        getApplicationContext().startService(intent);
    }

    private void displayArtistFragment() {
        ArtistFragment artistFragment = new ArtistFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, parsedArtistInfo);
        artistFragment.setArguments(bundle);
        displayFragment(artistFragment, R.string.artist_info, ARTIST_FRAGMENT);
    }

    private void displayAlbumFragment(String album) {
        AlbumFragment albumFragment = new AlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, parsedArtistInfo);
        bundle.putString(Constants.ALBUM_TO_VIEW, album);
        albumFragment.setArguments(bundle);
        displayFragment(albumFragment, R.string.album_info, Constants.OTHER_FRAGMENTS);
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
}
