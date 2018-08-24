package com.ks.musicdownloader;

public class Constants {

    // main activity - intent extra string
    public static final String DOWNLOAD_URL = "com.ks.musicdownloader.DOWNLOAD_URL";

    private String TEST_URL = "https://allthemwitches.bandcamp.com/album/our-mother-electricity";

    // bandcamp
    public static final String BANDCAMP_URL_REGEX = "(https:\\/\\/)?.*bandcamp\\.com\\/.*";
    public static final String BANDCAMP_TRALBUM_REGEX = "(?<=var\\sTralbumData\\s=\\s)(.)*?(?=};)";
}
