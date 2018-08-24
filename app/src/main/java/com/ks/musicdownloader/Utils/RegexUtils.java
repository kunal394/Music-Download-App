package com.ks.musicdownloader.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

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
}
