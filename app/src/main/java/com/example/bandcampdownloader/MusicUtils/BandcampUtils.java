package com.example.bandcampdownloader.MusicUtils;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class BandcampUtils {

    private static final String TAG = BandcampUtils.class.getSimpleName();
    private static final Integer SUCCESS_CODE = 200;
    private static final String EMPTY_STRING = "";

    public static boolean isAValidBandcampUrl(URL url) {
        return true;
    }

    /**
     * This function takes a bandcamp url and returns the response object if the url exists, otherwise returns null
     * @param bandcampUrl the bandcamp url to check
     */
    public static String getDataIfUrlExists(String bandcampUrl) throws IOException {
        URL url = new URL(bandcampUrl);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        Integer status = con.getResponseCode();
        if(!status.equals(SUCCESS_CODE)) {
            return EMPTY_STRING;
        }
        return con.getContent().toString();
    }

}
