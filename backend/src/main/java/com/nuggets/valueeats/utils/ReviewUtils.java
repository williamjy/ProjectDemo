package com.nuggets.valueeats.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewUtils {

    /**
    * This utility method is used for checking if review rating is valid.
    * 
    * @param    rating  A float containing a rating.
    * @return   A boolean of whether the rating is valid or not.
    */
    public static boolean isValidRating(Float rating) {
        return rating % 0.5 == 0 && rating >= 0.5 && rating <= 5;
    }

    /**
    * This utility method is used for checking if review message is valid.
    * 
    * @param    rating  A string containing a message.
    * @return   A boolean of whether the message is valid or not.
    */
    public static boolean isValidMessage(String message) {
        return message.length() <= 280;
    }

    /**
    * This utility method is used for creating a HashMap containing review details.
    * 
    * @param    id              An id that uniquely identifies a review.
    * @param    pic             A base64 string containing the profile picture of the diner.
    * @param    name            A string containing the name of the diner.
    * @param    message         A string containing the review message.
    * @param    rating          A float containing the review rating.
    * @param    eateryId        A long containing the eatery id.
    * @param    reviewPhotos    An arraylist containing a list of base64 strings of review photos.
    * @param    eateryName      A string containing the eatery name.
    * @return   A HashMap of the review details.
    * @see      VoucherEatingStyle
    */
    public static HashMap<String, Object> createReview(Long id, String pic, String name, String message, float rating, Long eateryId, ArrayList<String> reviewPhotos, String eateryName) {
        HashMap<String, Object> review = new HashMap<String, Object>();
        review.put("reviewId", id);
        review.put("profilePic", pic);
        review.put("name", name);
        review.put("rating", rating);
        review.put("message", message);
        review.put("eateryId", eateryId);
        review.put("reviewPhotos", reviewPhotos);
        review.put("eateryName", eateryName);
        return review;
    }
}
