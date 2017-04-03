package com.nicearma.utils;


import java.util.regex.Pattern;

public class Regex {
    /**
     *
     */
    public static final String regexDomain = "^https?://(www)?\\.?";
    /**
     *
     */
    public static final String regexImage = ".*\\.(png|jpg|jpeg|gif|png|svg)$";
    /**
     *
     */
    public static final String regexJsOrCss = ".*\\.(js|css)+\\??.*";

    /**
     *
     */
    public static Pattern getPatternDomain(String domain) {
        return Pattern.compile(regexDomain + domain + ".*", Pattern.CASE_INSENSITIVE);
    }

    /**
     *
     */
    public static Pattern getPatternImage() {
        return Pattern.compile(regexImage, Pattern.CASE_INSENSITIVE);
    }

    /**
     *
     */
    public static Pattern getPatternJsOrCss() {
        return Pattern.compile(regexJsOrCss, Pattern.CASE_INSENSITIVE);
    }

    /**
     *
     */
    public static boolean isSameUrl(String baseUrl, String url) {
        return url.contains(baseUrl + "#") || url.contains(baseUrl + "?");
    }

    /**
     *
     */
    public static boolean isSameUrl(String url) {
        return url.contains("/#") || url.contains("/?");
    }


}
