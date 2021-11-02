package com.nuggets.valueeats.controller;

import com.nuggets.valueeats.controller.decorator.token.CheckUserToken;
import com.nuggets.valueeats.entity.Diner;
import com.nuggets.valueeats.entity.Eatery;
import com.nuggets.valueeats.entity.User;
import com.nuggets.valueeats.service.CuisineService;
import com.nuggets.valueeats.service.UserManagementService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = ControllerConstants.URL)
@RestController
public class UserManagementController {
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private CuisineService cuisineService;

    /**
    * This controller method is used to map POST requests of the server's
    * /login route. This endpoint is intended to invoke the service
    * login() method, and with valid input, will return a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    user   A User object consisting of an email and password.
    * @return   An appropriate error message or success message.
    * @see      User
    */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> login(@RequestBody final User user) {
        return userManagementService.login(user);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * /register/diner route. This endpoint is intended to invoke the service
    * registerDiner() method, and with valid input, will return a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    diner   A Diner object consisting of an alias, email, password and optional profilePic.
    * @return   An appropriate error message or success message.
    * @see      Diner
    */
    @RequestMapping(value = "register/diner", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> registerDiner(@RequestBody final Diner diner) {
        return userManagementService.registerDiner(diner);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * /register/eatery route. This endpoint is intended to invoke the service
    * registerEatery() method, and with valid input, will return a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    eatery   A Eatery object consisting of an alias, email, password, address and optional profilePic,
    *                   cuisines and menuPhotos.
    * @return   An appropriate error message or success message.
    * @see      Eatery
    */
    @RequestMapping(value = "register/eatery", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> registerEatery(@RequestBody final Eatery eatery) {
        return userManagementService.registerEatery(eatery);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * /update/diner route. This endpoint is intended to invoke the service
    * updateDiner() method, and with valid input, will return a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an diner
    * @param    diner   A Diner object consisting of the details to be updated.
    *                       This can include email, password, alias and profilePic.
    * @return   An appropriate error message or success message.
    * @see      Diner
    */
    @RequestMapping(value = "update/diner", method = RequestMethod.POST)
    @CheckUserToken
    public ResponseEntity<JSONObject> updateDiner(
            @RequestHeader(name = "Authorization") String token, @RequestBody final Diner diner) {
        return userManagementService.updateDiner(diner, token);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * /update/eatery route. This endpoint is intended to invoke the service
    * updateEatery() method, and with valid input, will return a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an eatery
    * @param    eatery   A Eatery object consisting of the details to be updated.
    *                       This can include email, password, alias, profilePic, menuPhotos, cuisines and address.
    * @return   An appropriate error message or success message.
    * @see      Eatery
    */
    @RequestMapping(value = "update/eatery", method = RequestMethod.POST)
    @CheckUserToken
    public ResponseEntity<JSONObject> updateEatery(
            @RequestHeader(name = "Authorization") String token, @RequestBody final Eatery eatery) {
        return userManagementService.updateEatery(eatery, token);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * /logout route. This endpoint is intended to invoke the service
    * logout() method, and with valid input, will return a success message.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an user.
    * @return   An appropriate error message or success message.
    */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @CheckUserToken
    public ResponseEntity<JSONObject> logout(@RequestHeader(name = "Authorization") String token) {
        return userManagementService.logout(token);
    }

    /**
    * This controller method is used to map GET requests of the server's
    * /list/cuisines route. This endpoint is intended to invoke the service
    * listCuisines() method.
    * 
    * @return   A list of cuisines.
    */
    @RequestMapping(value = "list/cuisines", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> listCuisines() {
        return cuisineService.listCuisines();
    }

    /**
    * This controller method is used to map GET requests of the server's
    * /eatery/profile/details route. This endpoint is intended to invoke the service
    * getEateryProfile() method, and with valid input, will return a success response containing
    * the details of the eatery.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an eatery.
    * @param    id      An id that uniquely identifies an eatery.
    * @return   An appropriate error message or success response containing the eatery details.
    */
    @RequestMapping(value = "eatery/profile/details", method = RequestMethod.GET)
    @CheckUserToken
    public ResponseEntity<JSONObject> getEateryProfile(
            @RequestHeader(name = "Authorization") String token, @RequestParam(required = false) Long id) {
        return userManagementService.getEateryProfile(id, token);
    }

    /**
    * This controller method is used to map GET requests of the server's
    * /diner/profile/details route. This endpoint is intended to invoke the service
    * getDinerProfile() method, and with valid input, will return a success response containing
    * the details of the diner.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an diner.
    * @return   An appropriate error message or success response containing the diner details.
    */
    @RequestMapping(value = "diner/profile/details", method = RequestMethod.GET)
    @CheckUserToken
    public ResponseEntity<JSONObject> getDinerProfile(@RequestHeader(name = "Authorization") String token) {
        return userManagementService.getDinerProfile(token);
    }
}
