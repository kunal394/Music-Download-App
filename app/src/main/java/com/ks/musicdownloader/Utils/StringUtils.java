package com.ks.musicdownloader.Utils;

/**
 * Created by Kunal Singh(knl.singh) on 17-10-2018.
 */
@SuppressWarnings("DanglingJavadoc")
public class StringUtils {

    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";

    private StringUtils() {
        // enforcing non-instantiability since it is a utility class
    }

    public static boolean isEmpty(String s) {
        return s == null || s.equals(emptyString());
    }

    public static boolean isEmptyOrNull(String s) {
        return isEmpty(s) || isNull(s);
    }

    public static String emptyString() {
        return EMPTY_STRING;
    }

    public static String add(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String arg : strings) {
            builder.append(arg);
        }
        return builder.toString();
    }

    /******************Private************************************/
    /******************Methods************************************/

    private static boolean isNull(String s) {
        return !isEmpty(s) && NULL_STRING.equals(s);
    }
}
