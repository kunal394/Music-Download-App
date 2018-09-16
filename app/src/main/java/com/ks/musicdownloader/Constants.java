package com.ks.musicdownloader;

import android.Manifest;
import android.os.Environment;

public class Constants {

    // Permissions Constants
    public static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    // Intent extra strings
    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";
    public static final String MUSIC_SITE = "MUSIC_SITE";
    public static final String PARSE_ERROR_ACTION_KEY = "PARSER_ERROR_ACTION_KEY";
    public static final String PARSE_ERROR_MESSAGE_KEY = "PARSER_ERROR_MESSAGE_KEY";
    public static final String PARSE_ERROR_NULL_INTENT = "Null intent found!";
    public static final String PARSE_SUCCESS_ACTION_KEY = "PARSE_SUCCESS_ACTION_KEY";
    public static final String PARSE_SUCCESS_MESSAGE_KEY = "PARSE_SUCCESS_MESSAGE_KEY";
    public static final String PARSED_ARTIST_INFO = "PARSED_ARTIST_INFO";

    // Toast Messages and validator status
    public static final String NO_EXTERNAL_STORAGE_PERMISSION_MESSAGE = "No permission to save downloaded files!";
    public static final String NO_INTERNET_MESSAGE = "No internet is available. Please check your connection and retry!";
    public static final String NO_URL_PROVIDED_MESSAGE = "No url provided. Please provide a valid url and retry!";
    public static final String INVALID_URL_MESSAGE = "The provided url is invalid!";
    public static final String UNSUPPORTED_SITE_MESSAGE = "The url is either unsupported or has no media in it!";
    public static final String NON_EXISTENT_URL_MESSAGE = "The provided url does not exists.";
    public static final String VALID_URL_MESSAGE = "VALID_URL";
    public static final String PARSE_ERROR_MESSAGE = "Error while extracting songs from the url!";

    // String constants
    public static final String EMPTY_STRING = "";
    public static final String NULL_STRING = "null";

    // Request methods
    public static final String REQUEST_HEAD = "HEAD";
    public static final String URL_HTTP_PART = "http://";
    public static final String URL_HTTPS_PART = "https://";

    // Bandcamp
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
    public static final String MUSIC_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();

    // Artist info
    public static final ArtistInfo DUMMY_ARTIST_INFO = new ArtistInfo();
    public static final String SINGLES_ALBUM = "Singles";

    // Looper Constants
    public static final String DOWNLOAD_THREAD = "DOWNLOAD_THREAD";
    public static final int ENQUEUE_SONGS = 1;
    public static final int PARSING_PROGRESS = 1;
    public static final int HIDE_PROGRESS_BAR = 2;
    public static final int VALIDATING_PROGRESS = 3;
    public static final int PARSE_ERROR = 4;
}
