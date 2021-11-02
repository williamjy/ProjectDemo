package com.nuggets.valueeats.utils;

import org.apache.commons.codec.digest.DigestUtils;

public final class EncryptionUtils {

    /**
    * This utility method is used for encrypting a string.
    * 
    * @param    string      A string to be encrpyted.
    * @param    secret      A secret string used for encryption.
    * @return   An encrypted string.
    */
    public static String encrypt(final String string, final String secret) {
        return DigestUtils.sha256Hex(string + secret);
    }
}
