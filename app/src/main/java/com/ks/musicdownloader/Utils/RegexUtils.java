package com.ks.musicdownloader.Utils;

import android.util.Log;
import android.util.Patterns;

import com.ks.musicdownloader.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("DanglingJavadoc")
public class RegexUtils {

    private static final String TAG = RegexUtils.class.getSimpleName();

    public static boolean isAValidUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static boolean isRegexMatching(String regexPattern, String text) {
        return Pattern.compile(regexPattern).matcher(text).matches();
    }

    public static String getFirstRegexResult(String pattern, String text) {
        return getNthRegexResult(pattern, text, 1);
    }

    public static String prependHTTPSPartIfNotPresent(String url) {
        if (!startsWithHTTP(url) && !startsWithHTTPS(url)) {
            url = Constants.URL_HTTPS_PART + url;
        }
        return url;
    }

    public static boolean startsWithHTTP(String url) {
        return url.startsWith(Constants.URL_HTTP_PART);
    }

    /******************Private************************************/
    /******************Methods************************************/

    private static boolean startsWithHTTPS(String url) {
        return url.startsWith(Constants.URL_HTTPS_PART);
    }

    private static String getNthRegexResult(String pattern, String text, int pos) {
        String matched = Constants.EMPTY_STRING;
        if (pos < 1) {
            Log.d(TAG, "Invalid result position: " + pos);
            return matched;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        while (m.find()) {
            matched = m.group();
            pos--;
            if (pos == 0) {
                return matched;
            }
        }
        return matched;
    }
}
