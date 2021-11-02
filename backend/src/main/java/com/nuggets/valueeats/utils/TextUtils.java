package com.nuggets.valueeats.utils;

public final class TextUtils {

    /**
    * This utility method is used to capitalise the first letter of a string
    * and remove trailing spaces.
    * 
    * @param    string  A string to be converted.
    * @return   String with capitalised first letter and no trailing spaces.
    */
    public static String toTitle(String string) {
        string = string.trim();
        string = string.toLowerCase();
        string = string.substring(0, 1).toUpperCase();

        return string;
    }
}
