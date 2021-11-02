package com.nuggets.valueeats.controller;

import com.nuggets.valueeats.controller.decorator.token.CheckUserToken;
import com.nuggets.valueeats.controller.model.VoucherInput;
import com.nuggets.valueeats.service.EateryService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = ControllerConstants.URL)
@RestController
public class EateryController {
    @Autowired
    private EateryService eateryService;


    /**
    * This controller method is used to map POST requests of the server's
    * eatery/voucher route. This endpoint is intended to invoke the service
    * createVoucher() method, and with valid input, will return a success response.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an eatery
    * @param    voucher A voucherInput object that is created through RequestBody mapping.
                        A valid voucherInput object consists of eatingStyle, discount, quantity,
                            isRecurring, date, startMinute and endMinute.
    * @return   An appropriate error message or success message.
    * @see      VoucherInput
    */
    @RequestMapping(value = "eatery/voucher", method = RequestMethod.POST)
    @CheckUserToken
    public ResponseEntity<JSONObject> createVoucher(
            @RequestHeader(name = "Authorization") String token, @RequestBody VoucherInput voucher) {
        return eateryService.createVoucher(voucher, token);
    }

    /**
    * This controller method is used to map PUT requests of the server's
    * eatery/voucher route. This endpoint is intended to invoke the service
    * editVoucher() method, and with valid input, will return a success response.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param    token   An authentication token that is unique to an eatery
    * @param    voucher A voucherInput object that is created through RequestBody mapping.
                        A valid voucherInput object consists of id, eatingStyle, discount, quantity,
                            isRecurring, date, startMinute and endMinute.
    * @return           An appropriate error message or success message.
    * @see      VoucherInput
    * @see      RequestBody
    */
    @RequestMapping(value = "eatery/voucher", method = RequestMethod.PUT)
    @CheckUserToken
    public ResponseEntity<JSONObject> editVouchers(
            @RequestHeader(name = "Authorization") String token, @RequestBody VoucherInput voucher) {
        return eateryService.editVoucher(voucher, token);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * eatery/voucher route. This endpoint is intended to invoke the service
    * deleteVoucher() method, and with valid input, will return a success response.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param  token     An authentication token that is unique to an eatery
    * @param  id        An ID that is unique to a voucher
    * @return           An appropriate error message or success message
    */
    @RequestMapping(value = "eatery/voucher", method = RequestMethod.DELETE)
    @CheckUserToken
    public ResponseEntity<JSONObject> deleteVoucher(
            @RequestHeader(name = "Authorization") String token, @RequestParam Long id) {
        return eateryService.deleteVoucher(id, token);
    }

    /**
    * This controller method is used to map POST requests of the server's
    * eatery/verify/voucher route. This endpoint is intended to invoke the service
    * verifyVoucher() method, and with valid input, will return a success response.
    * An appropriate response indicating an error will be given when there is invalid input.
    * 
    * @param  token     An authentication token that is unique to an eatery
    * @param  code      A discount code that is unique to a booking
    * @return           An appropriate error message or success message
    */
    @RequestMapping(value = "eatery/verify/voucher", method = RequestMethod.POST)
    @CheckUserToken
    public ResponseEntity<JSONObject> verifyVoucher(
            @RequestHeader(name = "Authorization") String token, @RequestParam String code) {
        return eateryService.verifyVoucher(code, token);
    }
}
