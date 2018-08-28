package com.ks.musicdownloader;

public class Constants {

    // Intent extra strings
    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";
    public static final String SITE_NAME = "SITE_NAME";

    // Toast Messages
    public static final String UNSUPPORTED_SITE = "The website, to which the provided url belongs, is unsupported!";
    public static final String INVALID_URL = "The provided url is invalid!";
    public static final String NO_INTERNET = "No internet is available. Please check your connection and retry!";

    public static final String EMPTY_STRING = "";
    public static String TEST_URL = "https://allthemwitches.bandcamp.com/album/our-mother-electricity";

    // bandcamp
    public static final String BANDCAMP_URL_REGEX = "(https:\\/\\/)?.*bandcamp\\.com\\/.*";
    public static final String BANDCAMP_TRALBUM_REGEX = "(?<=var\\sTralbumData\\s=\\s)(.)*?(?=};)";
}
