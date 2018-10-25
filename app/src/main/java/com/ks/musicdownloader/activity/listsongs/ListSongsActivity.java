package com.ks.musicdownloader.activity.listsongs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.Utils.CommonUtils;
import com.ks.musicdownloader.Utils.LogUtils;
import com.ks.musicdownloader.activity.DrawerActivityWithFragment;
import com.ks.musicdownloader.activity.common.ArtistInfo;
import com.ks.musicdownloader.activity.common.Constants;
import com.ks.musicdownloader.activity.common.FragmentCallback;
import com.ks.musicdownloader.service.DownloaderService;
import com.ks.musicdownloader.songsprocessors.MusicSite;

@SuppressWarnings("DanglingJavadoc")
public class ListSongsActivity extends DrawerActivityWithFragment implements FragmentCallback {

    private static final String TAG = ListSongsActivity.class.getSimpleName();

    private static final int ARTIST_FRAGMENT = 1;

    private ArtistInfo parsedArtistInfo;
    private MusicSite musicSite;

    private boolean downloading;

    // TODO: 17-10-2018 add ongoing downloads and completed downloads in drawer. long press on the items of these fragments
    // should give the option to copy the url, while tap opens up the status of ongoing downloads

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtils.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean backPressed() {
        LogUtils.d(TAG, "onBackPressed: ");
        if (CURRENTLY_SELECTED_FRAGMENT != ARTIST_FRAGMENT) {
            displayArtistFragment();
            return true;
        }
        return false;
    }

    @Override
    public void download() {
        if (downloading) {
            return;
        }
        downloading = true;
//        startDownloaderService();
    }

    @Override
    protected void inflateActivitySpecificMenu() {
        musicSite.createNavMenu(navigationView, parsedArtistInfo);
    }

    @Override
    protected void displayInitialFragment() {
        displayArtistFragment();
    }

    @Override
    protected void init() {
        getIntentExtras();
    }

    @Override
    protected void checkForActivityRelatedMenuItems(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.string.artist_item_id:
                displayArtistFragment();
                break;
            case R.string.album_item_id:
                displayAlbumFragment(menuItem.getTitle().toString());
                break;
        }
    }

    @Override
    protected void performPreCheckOnNav() {
        if (CURRENTLY_SELECTED_FRAGMENT == ARTIST_FRAGMENT) {
            markMenuItemChecked(R.string.artist_item_id, false);
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void getIntentExtras() {
        LogUtils.d(TAG, "getIntentExtras(): ");
        Intent intent = getIntent();
        parsedArtistInfo = intent.getParcelableExtra(Constants.PARSED_ARTIST_INFO);
        String siteName = intent.getStringExtra(Constants.MUSIC_SITE);
        LogUtils.d(TAG, "getIntentExtras() sitename: " + siteName);
        musicSite = Enum.valueOf(MusicSite.class, siteName);
        LogUtils.d(TAG, "getIntentExtras() artInfo: " + parsedArtistInfo.toString());
        if (intent.hasExtra(Constants.NOTI_ID_KEY)) {
            LogUtils.d(TAG, "getIntentExtras()");
            CommonUtils.cancelNotification(this, Integer.valueOf(intent.getStringExtra(Constants.NOTI_ID_KEY)));
        }
    }

    private void startDownloaderService() {
        LogUtils.d(TAG, "startDownloaderService: ");
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
        markMenuItemChecked(R.string.artist_item_id, true);
    }

    private void displayAlbumFragment(String album) {
        AlbumFragment albumFragment = new AlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARSED_ARTIST_INFO, parsedArtistInfo);
        bundle.putString(Constants.ALBUM_TO_VIEW, album);
        albumFragment.setArguments(bundle);
        displayFragment(albumFragment, R.string.album_info, Constants.OTHER_FRAGMENTS);
    }
}
