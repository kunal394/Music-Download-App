package com.ks.musicdownloader;

public class Constants {

    // bandcamp
    public static final String BANDCAMP_URL_REGEX = "(https:\\/\\/)?.*bandcamp\\.com\\/.*";
    public static final String BANDCAMP_TRALBUM_REGEX = "(?<=var\\sTralbumData\\s=\\s)(.)*?(?=};)";
}
