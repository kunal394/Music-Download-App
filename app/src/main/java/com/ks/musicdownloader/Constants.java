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
    public static final String BANDCAMP_TRALBUM_REGEX = "(?<=var\\sTralbumData\\s=\\s)(.)*?(?=\\};)";
    public static final String BANDCAMP_TYPE_REGEX = "\\\"type\\\"\\:[^\\,]*";
    public static final String BANDCAMP_TYPE_TRACK = "track";
    public static final String BANDCAMP_TYPE_ALBUM = "album";

    //Directories
    public static final String MUSIC_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();

    // Artist info
    public static final ArtistInfo DUMMY_ARTIST_INFO = new ArtistInfo();
}
