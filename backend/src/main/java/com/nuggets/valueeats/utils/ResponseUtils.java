package com.nuggets.valueeats.utils;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class ResponseUtils {

    /**
    * This utility method is used for creating a JSONObject with a message.
    * 
    * @param    message      A string that contains a message.
    * @return   A JSONObject with a message.
    */
    public static JSONObject createResponse(final String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return new JSONObject(response);
    }

    /**
    * This utility method is used for creating a JSONObject with a message and data.
    * 
    * @param    message      A string that contains a message.
    * @param    result      A string that contains a message and data.
    * @return   A JSONObject with a message and data.
    */
    public static JSONObject createResponse(final String message, final JSONObject result) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("data", result);

        return new JSONObject(response);
    }

    public static JSONObject createResponse(final JSONObject result) {
        return new JSONObject(result);
    }
}
