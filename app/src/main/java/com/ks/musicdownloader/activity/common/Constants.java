package com.ks.musicdownloader.activity.common;

import android.Manifest;
import android.os.Environment;

@SuppressWarnings("WeakerAccess")
public class Constants {

    // Permissions Constants
    public static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    // Intent Extra Strings
    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";
    public static final String MUSIC_SITE = "MUSIC_SITE";
    public static final String PARSE_ERROR_ACTION_KEY = "PARSE_ERROR_ACTION_KEY";
    public static final String PARSE_ERROR_MESSAGE_KEY = "PARSE_ERROR_MESSAGE_KEY";
    public static final String PARSE_ERROR_NULL_INTENT = "Null intent found!";
    public static final String PARSE_ERROR_NULL_ARTIST_INFO = "PARSE_ERROR_NULL_ARTIST_INFO";
    public static final String PARSE_SUCCESS_ACTION_KEY = "PARSE_SUCCESS_ACTION_KEY";
    public static final String PARSE_SUCCESS_MESSAGE_KEY = "PARSE_SUCCESS_MESSAGE_KEY";
    public static final String VALIDATE_ERROR_ACTION_KEY = "VALIDATE_ERROR_ACTION_KEY";
    public static final String VALIDATE_ERROR_MESSAGE_KEY = "VALIDATE_ERROR_MESSAGE_KEY";
    public static final String VALIDATE_ERROR_NULL_INTENT = "Null intent found!";
    public static final String VALIDATE_SUCCESS_ACTION_KEY = "VALIDATE_SUCCESS_ACTION_KEY";
    public static final String PARSED_ARTIST_INFO = "PARSED_ARTIST_INFO";
    public static final String ALBUM_TO_VIEW = "ALBUM_TO_VIEW";

    // Toast Messages and Validator Status
    public static final String NO_EXTERNAL_STORAGE_PERMISSION_MESSAGE = "No permission to save downloaded files!";
    public static final String NO_INTERNET_MESSAGE = "No internet is available. Please check your connection and retry!";
    public static final String NO_URL_PROVIDED_MESSAGE = "No url provided. Please provide a valid url and retry!";
    public static final String INVALID_URL_MESSAGE = "The provided url is invalid!";
    public static final String UNSUPPORTED_SITE_MESSAGE = "The url is either unsupported or has no media in it!";
    public static final String NON_EXISTENT_URL_MESSAGE = "The provided url does not exists.";
    public static final String PARSE_ERROR_MESSAGE = "Error while extracting songs from the url!";
    public static final String PARSING_IN_PROGRESS = "Previous extraction is already in progress.";

    // Request Methods
    public static final String REQUEST_HEAD = "HEAD";
    public static final String URL_HTTP_PART = "http://";
    public static final String URL_HTTPS_PART = "https://";

    // Bandcamp Constants
    public static final String BANDCAMP_URL_REGEX = "(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com\\/?.*";
    public static final String BANDCAMP_ARTIST_URL_REGEX = "^(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com((\\/$)|(\\/music$))?";
    public static final String BANDCAMP_ALBUM_URL_REGEX = "^(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com\\/album\\/.*";
    public static final String BANDCAMP_TRACK_URL_REGEX = "^(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com\\/track\\/.*";
    public static final String BANDCAMP_TRALBUM_REGEX = "(?<=var\\sTralbumData\\s=\\s)(.)*?(?=\\};)"; // might come to use later
    public static final String BANDCAMP_TRACK_INFO_REGEX = "trackinfo\\: (.*?)\\}\\]"; // match everything till "}]" is found
    public static final String BANDCAMP_ARTIST_SELECTOR_FOR_ARTIST = "span.title";
    public static final String BANDCAMP_ARTIST_SELECTOR_FOR_ALBUM_AND_TRACK = "span[itemprop=byArtist]";
    public static final String BANDCAMP_TRACK_TITLE_SELECTOR = "h2.trackTitle";
    public static final String BANDCAMP_ALBUM_LIST_SELECTOR = "ol[data-initial-values]";
    public static final String BANDCAMP_TRACK_INFO_KEY = "trackinfo";
    public static final String BANDCAMP_TITLE_KEY = "title";
    public static final String BANDCAMP_FILE_KEY = "file";
    public static final String BANDCAMP_MP3_KEY = "mp3-128";
    public static final String BANDCAMP_ARTIST_TEST_URL = "https://naxatras.bandcamp.com/";
    public static final String BANDCAMP_ALBUM_TEST_URL = "https://naxatras.bandcamp.com/album/naxatras";
    public static final String BANDCAMP_TRACK_TEST_URL = "https://ommosound.bandcamp.com/track/plutesc-in-aer";

    //Directories
    public static final String MUSIC_DIRECTORY = Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_MUSIC).getAbsolutePath();
    public static final String EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();

    // Artist info
    public static final ArtistInfo DUMMY_ARTIST_INFO = new ArtistInfo();
    public static final String SINGLES_ALBUM = "Singles";

    // Looper Constants
    public static final String DOWNLOAD_THREAD = "DOWNLOAD_THREAD";
    public static final int ENQUEUE_SONGS = 1;
    public static final int PARSING_COMPLETE = 0;
    public static final int PARSING_PROGRESS = 1;
    public static final int VALIDATING_PROGRESS = 2;
    public static final int PARSE_ERROR = 4;
    public static final int HIDE_LAST_SEARCH_VIEW = 5;
    public static final int DISPLAY_LAST_SEARCH_VIEW = 6;

    // Class Local Constants
    public static final String AUDIO_MIME_TYPE = "audio/*";
    public static final String GITHUB_REPO_URI = "https://github.com/kunal394/Music-Download-App";
    public static String MP3_EXTENSION = ".mp3";
    public static final int OTHER_FRAGMENTS = 2;
    public static final int NO_FRAGMENT = 0;

    // Shared Pref Constants
    public static final String SETTINGS_PREF_NAME = "settings_preferences";
    public static final String SEARCH_PREF_NAME = "search_preferences";
    public static final String PREF_SELECT_ALL_KEY = "PREF_SELECT_ALL_KEY";
    public static final String PREF_LAST_FETCHED_URL_KEY = "PREF_LAST_FETCHED_URL_KEY";
    public static final String PREF_DEFAULT_SONGS_FOLDER_KEY = "PREF_DEFAULT_SONGS_FOLDER_KEY";
    public static final String PREF_PARSING_STATUS_KEY = "PREF_PARSING_STATUS_KEY";

    // Request codes for activities
    public static final Integer SET_DEFAULT_FOLDER_REQUEST_CODE = 900;

    // Notification Constants
    public static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "DEFAULT_CHANNEL_NAME";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION = "DEFAULT_CHANNEL_DESC";
    public static final String LIST_SONGS_NOTIFICATION_TITLE = "Parsing Complete";
    public static final String LIST_SONGS_NOTIFICATION_CHANNEL_ID = "list_songs_channel";
    public static final Integer LIST_SONGS_NOTIFICATION_ID = 100;
    public static final int PENDING_INTENT_DEFAULT_REQ_CODE = 10;
    public static final long DEFAULT_NOTIFICATION_TIMEOUT = 120000;
    public static final String NOTI_ID_KEY = "NOTI_ID_KEY";

    // Menu Constants
    public static final Integer ARTIST_INFO_MENU_PRIORITY = 100; // lower the priority, higher the menu item's position
    public static final Integer ALBUM_INFO_MENU_PRIORITY = 101;
}
