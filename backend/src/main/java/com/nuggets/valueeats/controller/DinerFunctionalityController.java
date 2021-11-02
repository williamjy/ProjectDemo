package com.nuggets.valueeats.controller;

import com.nuggets.valueeats.controller.decorator.token.CheckUserToken;
import com.nuggets.valueeats.entity.Review;
import com.nuggets.valueeats.service.DinerFunctionalityService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = ControllerConstants.URL)
@RestController
public class DinerFunctionalityController {
    
    @Autowired
    private DinerFunctionalityService dinerFunctionalityService;

    /**
    * This controller method is used to map POST requests of the server's
    * diner/createreview route. This endpoint is intended to invoke the service
    * createReview() method, and with valid input, will return a success response.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an diner
    * @param    review  A Review object that is created through RequestBody mapping.
                        A valid Review object consists of rating and eateryId, and can contain
    *                       message and reviewPhotos.
    * @return   An appropriate error message or success message.
    * @see      Review
    */
    @RequestMapping(value = "diner/createreview", method = RequestMethod.POST)
    @CheckUserToken
    public ResponseEntity<JSONObject> createReview(
            @RequestHeader(name = "Authorization") String token, @RequestBody Review review) {
        return dinerFunctionalityService.createReview(review, token);
    }

    /**
    * This controller method is used to map DELETE requests of the server's
    * diner/removereview route. This endpoint is intended to invoke the service
    * removeReview() method, and with valid input, will return a success response.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an diner
    * @param    review  A Review object that is created through RequestBody mapping.
                        A valid Review object consists of id and eateryId.
    * @return   An appropriate error message or success message.
    * @see      Review
    */
    @RequestMapping(value = "diner/removereview", method = RequestMethod.DELETE)
    @CheckUserToken
    public ResponseEntity<JSONObject> removeReview(
            @RequestHeader(name = "Authorization") String token, @RequestBody Review review) {
        return dinerFunctionalityService.removeReview(review, token);
    }

    /**
    * This controller method is used to map GET requests of the server's
    * list/eateries route. This endpoint is intended to invoke the service
    * listEateries() method, and with valid input, will return a success response containing
    * a list of eateries and a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token  An authentication token that is unique to a diner.
    * @param    sort  A string which is either "Distance", "New", or "Rating" to determine eatery sorting.
    * @param    latitude  The latitude of the diner's location if using distance sorting.
    * @param    longitude  The longitude of the diner's location if using distance sorting.
    * @return   An error response with error message or a success response with list of eateries.
    */
    @RequestMapping(value = "list/eateries", method = RequestMethod.GET)
    @CheckUserToken
    public ResponseEntity<JSONObject> listEateries(
            @RequestHeader(name = "Authorization") String token, @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude) {
        return dinerFunctionalityService.listEateries(token, sort, latitude, longitude);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * diner/editreview route. This endpoint is intended to invoke the service
    * editReview() method, and with valid input, will return a success response.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an diner
    * @param    review  A Review object that is created through RequestBody mapping.
                        A valid Review object consists of id, rating and eateryId and can contain
    *                       message and reviewPhotos.
    * @return   An appropriate error message or success message.
    * @see      Review
    */
    @RequestMapping(value = "diner/editreview", method = RequestMethod.POST)
    @CheckUserToken
    public ResponseEntity<JSONObject> editReview(
            @RequestHeader(name = "Authorization") String token, @RequestBody Review review) {
        return dinerFunctionalityService.editReview(review, token);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * diner/book route. This endpoint is intended to invoke the service
    * bookVoucher() method, and with valid input, will return a success response containing
    * booking details and a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an diner
    * @param    id      A unique id identifying a voucher.
    * @return   An appropriate error message or success message.
    */
    @RequestMapping(value = "diner/book", method = RequestMethod.POST)
    @CheckUserToken
    public ResponseEntity<JSONObject> bookVoucher(
            @RequestHeader(name = "Authorization") String token, @RequestParam Long id) {
        return dinerFunctionalityService.bookVoucher(id, token);
    }


    /**
    * This controller method is used to map GET requests of the server's
    * diner/voucher route. This endpoint is intended to invoke the service
    * dinerListVouchers() method, and with valid input, will return a success response containing
    * a list of diner vouchers and a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an diner
    * @return   An appropriate error message or success message.
    * @see      Review
    */
    @RequestMapping(value = "diner/voucher", method = RequestMethod.GET)
    @CheckUserToken
    public ResponseEntity<JSONObject> dinerListVouchers(@RequestHeader(name = "Authorization") String token) {
        return dinerFunctionalityService.dinerListVouchers(token);
    }
}
