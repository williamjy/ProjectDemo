package com.nuggets.valueeats.utils;

import java.util.regex.Pattern;

public class ValidationUtils {

    /**
    * This utility method is used to check if an email is valid
    * 
    * @param    email  A string containing email.
    * @return   A boolean of whether the email is valid.
    */
    public static boolean isValidEmail(final String email) {
        String regex = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
        return Pattern.matches(regex, email);
    }

    /**
    * This utility method is used to check if a password is valid
    * 
    * @param    password  A string containing password.
    * @return   A boolean of whether the password is valid.
    */
    public static boolean isValidPassword(final String password) {
        return password.length() >= 8 &&
                password.length() <= 32 &&
                !password.toLowerCase().equals(password) &&
                !password.toUpperCase().equals(password);
    }

    /**
    * This utility method is used to check if an alias is valid
    * 
    * @param    alias  A string containing alias.
    * @return   A boolean of whether the alias is valid.
    */
    public static boolean isValidAlias(final String alias) {
        return alias.length() > 0 && alias.length() <= 12;
    }
}
