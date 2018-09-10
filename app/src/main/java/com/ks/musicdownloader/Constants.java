package com.ks.musicdownloader;

import android.os.Environment;

public class Constants {

    // Intent extra strings
    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";
    public static final String SITE_NAME = "SITE_NAME";

    // Toast Messages and validator status
    public static final String NO_INTERNET_MESSAGE = "No internet is available. Please check your connection and retry!";
    public static final String NO_URL_PROVIDED_MESSAGE = "No url provided. Please provide a valid url and retry!";
    public static final String INVALID_URL_MESSAGE = "The provided url is invalid!";
    public static final String UNSUPPORTED_SITE_MESSAGE = "The url is either unsupported or has no media in it!";
    public static final String NON_EXISTENT_URL_MESSAGE = "The provided url does not exists.";
    public static final String VALID_URL_MESSAGE = "VALID_URL";

    public static final String EMPTY_STRING = "";

    // Request methods
    public static final String REQUEST_HEAD = "HEAD";
    public static final String URL_HTTP_PART = "http://";
    public static final String URL_HTTPS_PART = "https://";

    public static String TEST_URL = "https://allthemwitches.bandcamp.com/album/our-mother-electricity";

    // Bandcamp
    public static final String BANDCAMP_URL_REGEX = "(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com\\/?.*";
    public static final String BANDCAMP_ARTIST_URL_REGEX = "^(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com((\\/$)|(\\/music$))?";
    public static final String BANDCAMP_ALBUM_URL_REGEX = "^(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com\\/album\\/.*";
    public static final String BANDCAMP_TRACK_URL_REGEX = "^(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com\\/track\\/.*";
    public static final String BANDCAMP_TRALBUM_REGEX = "(?<=var\\sTralbumData\\s=\\s)(.)*?(?=\\};)";
    public static final String BANDCAMP_TRACK_INFO_REGEX = "trackinfo\\: \\[[^\\]]*\\]";
    public static final String BANDCAMP_SONG_DOWNLOAD_URL_REGEX = "\\\"mp3-128\\\"\\:[^\\,]*";
    public static final String BANDCAMP_ARTIST_SELECTOR_FOR_ARTIST = "span.title";
    public static final String BANDCAMP_ARTIST_SELECTOR_FOR_ALBUM_AND_TRACK = "span[itemprop=byArtist]";
    public static final String BANDCAMP_TRACK_TITLE_SELECTOR = "h2.trackTitle";
    public static final String BANDCAMP_ALBUM_LIST_SELECTOR = "ol[data-initial-values]";
    public static final String BANDCAMP_TRACK_INFO_KEY = "trackinfo";
    public static final String BANDCAMP_TITLE_KEY = "title";
    public static final String BANDCAMP_FILE_KEY = "file";
    public static final String BANDCAMP_MP3_KEY = "mp3-128";
    public static final String NULL_STRING = "null";

    //Directories
    public static final String MUSIC_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();

    // Artist info
    public static final ArtistInfo DUMMY_ARTIST_INFO = new ArtistInfo();
    public static final String DUMMY_ALBUM_NAME = "DUMMY_ALBUM_NAME";
}
