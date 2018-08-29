package com.ks.musicdownloader;

public class Constants {

    // Intent extra strings
    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";
    public static final String SITE_NAME = "SITE_NAME";

    // Toast Messages
    public static final String NO_INTERNET = "No internet is available. Please check your connection and retry!";
    public static final String NO_URL_PROVIDED = "No url provided. Please provide a valid url and retry!";
    public static final String INVALID_URL = "The provided url is invalid!";
    public static final String UNSUPPORTED_SITE = "The url is either unsupported or has no media in it!";

    public static final String EMPTY_STRING = "";

    // Request methods
    public static final String REQUEST_HEAD = "HEAD";
    public static final String REQUEST_GET = "GET";
    public static final String URL_HTTP_PART = "http://";
    public static final String URL_HTTPS_PART = "https://";

    public static String TEST_URL = "https://allthemwitches.bandcamp.com/album/our-mother-electricity";

    // bandcamp
    public static final String BANDCAMP_URL_REGEX = "(https?:\\/\\/)?([\\d|\\w]+)\\.bandcamp\\.com\\/?.*";
    public static final String BANDCAMP_TRALBUM_REGEX = "(?<=var\\sTralbumData\\s=\\s)(.)*?(?=};)";
}
