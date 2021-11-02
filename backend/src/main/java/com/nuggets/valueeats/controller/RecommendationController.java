package com.nuggets.valueeats.controller;

import com.nuggets.valueeats.controller.decorator.token.CheckDinerToken;
import com.nuggets.valueeats.service.RecommendationService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = ControllerConstants.URL)
@RestController
@RequestMapping(path = "/recommendation")
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    
    /**
    * This controller method is used to map GET requests of the server's
    * /recommendation/eatery/fuzzy_search/{search} route. This endpoint is intended to invoke the service
    * fuzzySearch() method, and will always return a response containing a
    * list of eateries that closely matches the search.
    * 
    * @param    token   An authentication token that is unique to an diner
    * @return   A list of eateries that closely matches the search.
    */
    @GetMapping("/eatery/fuzzy_search/{search}")
    @CheckDinerToken
    public ResponseEntity<JSONObject> fuzzySearch(
            @RequestHeader(name = "Authorization") String token, @PathVariable final String search) {
        return recommendationService.fuzzySearch(search);
    }

    
    /**
    * This controller method is used to map GET requests of the server's
    * /recommendation route. This endpoint is intended to invoke the service
    * recommendation() method, and with valid token will return a response containing a
    * list of eateries that is recommended for the diner
    * An appropriate response indicating an error will be given when the token is invalid.
    * 
    * @param    token   An authentication token that is unique to an diner
    * @return   A success response containing list of eateries that is recommended for the diner or an error message on failure.
    */
    @GetMapping("")
    @CheckDinerToken
    public ResponseEntity<JSONObject> recommendation(@RequestHeader(name = "Authorization") String token) {
        return recommendationService.recommendation(token);
    }
}
