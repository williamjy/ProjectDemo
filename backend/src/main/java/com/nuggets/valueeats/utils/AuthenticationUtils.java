package com.nuggets.valueeats.utils;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class AuthenticationUtils {

    /**
    * This utility method is used for creating a response when login is called.
    * 
    * @param    loginPassword   A string containing the inputted password.
    * @param    secret          A secret string that encrypts the password.
    * @param    actualPassword  An encrypted string that contains the real password.
    * @param    successMessage  A string that contains success message.
    * @param    isDiner         A string that defines if the user is a diner or eatery.
    * @param    dataMedium      HashMap to store response.
    * @return   An error message on failure or a success message when successful.
    */
    public static ResponseEntity<JSONObject> loginPasswordCheck(final String loginPassword, final String secret,
            final String actualPassword, final String successMessage,
            final boolean isDiner, Map<String, String> dataMedium) {
        if (EncryptionUtils.encrypt(loginPassword, secret).equals(actualPassword)) {

            dataMedium.put("type", (isDiner ? "diner" : "eatery"));
            JSONObject data = new JSONObject(dataMedium);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse(successMessage, data));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid password, please try again"));
    }
}
