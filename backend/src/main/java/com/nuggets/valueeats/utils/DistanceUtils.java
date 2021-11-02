package com.nuggets.valueeats.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

@Component
public class DistanceUtils {
    @Value("${security.google_api}")
    private String googleAPI;

    /**
    * This utility method is used for converting a distance to a distance string.
    * 
    * @param    distance   An integer containing distance in metres.
    * @return   A string of the distance with units.
    */
    public static String convertDistanceToString(Integer distance) {
        if (distance < 1000) {
            return distance + "m";
        }
        return String.format("%.2f", (float) distance / 1000) + "km";
    }

    /**
    * This utility method is used for obtaining the distance from a diner to a list of eateries.
    * 
    * @param    latitude        A double containing the diner's latitude location.
    * @param    longitude       A double containing the diner's longitude location.
    * @param    addressString   A string containing an combined and encoded list of eatery addresses.
    * @param    addresses       A string list of eatery addresses.
    * @return   A HashMap of eatery address and distance from diner.
    */
    public HashMap<String, Integer> findDistanceFromDiner(Double latitude, Double longitude, String addressString, List<String> addresses) {
        HashMap<String, Integer> addressDistanceFromDiner = new HashMap<>();

        try {
            OkHttpClient client = new OkHttpClient();
            String encodedLatitude = URLEncoder.encode(latitude.toString(), "UTF-8");
            String encodedLongitude = URLEncoder.encode(longitude.toString(), "UTF-8");
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + encodedLatitude + "," + encodedLongitude + "&destinations=" + addressString + "&key=" + googleAPI)
                    .get()
                    .build();
            com.squareup.okhttp.ResponseBody responseBody = client.newCall(request).execute().body();

            JSONParser parser = new JSONParser();
            Object response = parser.parse(responseBody.string());
            JSONObject map = (JSONObject) response;
            String status = (String) map.get("status");
            if (status.equals("OK")) {
                for (int i = 0; i < addresses.size(); i++) {
                    JSONObject elements = (JSONObject) ((JSONArray) ((JSONObject) ((JSONArray) map.get("rows")).get(0)).get("elements")).get(i);
                    String elementStatus = (String) elements.get("status");
                    if (elementStatus.equals("OK")) {
                        JSONObject distanceObj = (JSONObject) elements.get("distance");
                        Integer distance = Integer.parseInt(distanceObj.get("value").toString());

                        addressDistanceFromDiner.put(addresses.get(i), distance);
                    } else {

                        addressDistanceFromDiner.put(addresses.get(i), Integer.MAX_VALUE);
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {

            return null;
        }

        return addressDistanceFromDiner;
    }
}
