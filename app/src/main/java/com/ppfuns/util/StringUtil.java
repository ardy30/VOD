package com.ppfuns.util;

/**
 * Created by hmc on 2016/11/2.
 */

public class StringUtil {

    public static String replaceVertical(String text) {
        String regex = "|";
        String replaceMent = "  |  ";
        return replaceFormat(text, regex, replaceMent);
    }

    public static String replaceFormat(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }
}
