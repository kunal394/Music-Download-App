package com.ks.musicdownloader.Utils;

import android.util.Patterns;

import com.ks.musicdownloader.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static boolean isAValidUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static boolean isRegexMatching(String regexPattern, String text) {
        return Pattern.compile(regexPattern).matcher(text).matches();
    }

    public static String getFirstRegexResult(String pattern, String text) {
        String matched = "";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        if (m.find()) {
            matched = m.group();
        }
        return matched;
    }

    public static boolean startsWithHTTP(String url) {
        return url.startsWith(Constants.URL_HTTP_PART);
    }

    public static boolean startsWithHTTPS(String url) {
        return url.startsWith(Constants.URL_HTTPS_PART);
    }
}
