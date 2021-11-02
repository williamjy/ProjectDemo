package com.nuggets.valueeats.controller;

import com.nuggets.valueeats.controller.decorator.token.CheckUserToken;
import com.nuggets.valueeats.entity.Diner;
import com.nuggets.valueeats.entity.Eatery;
import com.nuggets.valueeats.entity.Review;
import com.nuggets.valueeats.entity.User;
import com.nuggets.valueeats.entity.voucher.RepeatedVoucher;
import com.nuggets.valueeats.entity.voucher.Voucher;
import com.nuggets.valueeats.utils.HealthService;
import com.nuggets.valueeats.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = ControllerConstants.URL)
@RestController
public class HealthController {
    @Autowired
    private HealthService healthService;
    @Autowired
    private JwtUtils jwtUtils;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "Test";
    }

    @RequestMapping(value = "/encode/{string}", method = RequestMethod.GET)
    public String encode(@PathVariable final String string) {
        String a = jwtUtils.encode(string);
        String b = jwtUtils.decode(a);

        return "String: " + string + "\nEncode: " + a + "\nDecode: " + b;
    }

    @RequestMapping(value = "health/list/diners", method = RequestMethod.GET)
    public List<Diner> listDiner() {
        return healthService.listDiner();
    }

    @RequestMapping(value = "health/list/eateries", method = RequestMethod.GET)
    public List<Eatery> listEateries() {
        return healthService.listEatery();
    }

    @RequestMapping(value = "health/list/users", method = RequestMethod.GET)
    public List<User> listUsers() {
        return healthService.listUser();
    }

    @RequestMapping(value = "health/list/reviews", method = RequestMethod.GET)
    public List<Review> listReviews() {
        return healthService.listReview();
    }

    @RequestMapping(value = "health/list/cuisine", method = RequestMethod.GET)
    public List<Object> listCuisines() {
        return healthService.listCuisines();
    }

    @RequestMapping(value = "test/checktoken", method = RequestMethod.POST)
    @CheckUserToken
    public String testAuth(@RequestHeader(name = "Authorization") String token, @RequestBody final Diner diner) {
        return token;
    }

    @RequestMapping(value = "health/list/repeatVoucher", method = RequestMethod.GET)
    public List<RepeatedVoucher> listRepeatVouchers() {
        return healthService.listRepeatVoucher();
    }

    @RequestMapping(value = "health/list/voucher", method = RequestMethod.GET)
    public List<Voucher> listVouchers() {
        return healthService.listVoucher();
    }
}
