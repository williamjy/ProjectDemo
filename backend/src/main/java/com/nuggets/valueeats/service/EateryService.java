package com.nuggets.valueeats.service;

import com.nuggets.valueeats.controller.model.VoucherInput;
import com.nuggets.valueeats.entity.BookingRecord;
import com.nuggets.valueeats.entity.Eatery;
import com.nuggets.valueeats.entity.voucher.RepeatedVoucher;
import com.nuggets.valueeats.entity.voucher.Voucher;
import com.nuggets.valueeats.repository.BookingRecordRepository;
import com.nuggets.valueeats.repository.EateryRepository;
import com.nuggets.valueeats.repository.voucher.RepeatVoucherRepository;
import com.nuggets.valueeats.repository.voucher.VoucherRepository;
import com.nuggets.valueeats.utils.JwtUtils;
import com.nuggets.valueeats.utils.ResponseUtils;
import com.nuggets.valueeats.utils.VoucherUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EateryService {
    private static final int MAX_VOUCHER_DURATION = 1440;
    private static final int MIN_VOUCHER_DURATION = 30;
    @Autowired
    private RepeatVoucherRepository repeatVoucherRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private EateryRepository eateryRepository;
    @Autowired
    private BookingRecordRepository bookingRecordRepository;
    @Autowired
    private JwtUtils jwtUtils;

    /**
    * This method is used to create an eatery voucher given a valid eatery token and also
    * a valid voucherInput object with the required fields.
    * @param    token           An authentication token that is unique to an eatery
    * @param    voucherInput    A valid VoucherInput object consists of eatingStyle, discount, quantity,
                                    isRecurring, date, startMinute and endMinute.
    * @return   An appropriate error message or success message.
    * @see      VoucherInput
    */
    @Transactional
    public ResponseEntity<JSONObject> createVoucher(VoucherInput voucherInput, String token) {

        String decodedToken = jwtUtils.decode(token);

        if (decodedToken == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }
        Long eateryId;
        try {
            eateryId = Long.valueOf(decodedToken);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Eatery ID is invalid"));
        }

        if (!eateryRepository.existsByToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is invalid."));
        }

        if (!eateryRepository.existsById(eateryId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Eatery does not exist"));
        }

        if (voucherInput.getEatingStyle() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Received a null eating style"));
        }
        if (voucherInput.getDiscount() == null || voucherInput.getDiscount() <= 0 || voucherInput.getDiscount() > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Discount is invalid"));
        }
        if (voucherInput.getQuantity() == null || voucherInput.getQuantity() < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid voucher quantity"));
        }
        if (voucherInput.getDate() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid date"));
        }
        if (voucherInput.getStartMinute() == null && voucherInput.getEndMinute() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Start and/or end minute missing"));
        }

        if (!VoucherUtils.isValidTime(voucherInput.getDate(), voucherInput.getStartMinute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Start time must not be in the past."));
        }

        if ((voucherInput.getEndMinute() - voucherInput.getStartMinute()) < MIN_VOUCHER_DURATION || (voucherInput.getEndMinute() - voucherInput.getStartMinute()) > MAX_VOUCHER_DURATION) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Vouchers must be at least 30 min long and cannot exceed 24 hours."));
        }

        if (voucherInput.getStartMinute() < 0 || voucherInput.getStartMinute() >= 1440 || voucherInput.getEndMinute() <= 0 || voucherInput.getStartMinute() >= voucherInput.getEndMinute()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid times"));
        }

        if (voucherInput.getIsRecurring() != null && voucherInput.getIsRecurring()) {
            return handleRecurringCreateVoucher(voucherInput, eateryId);
        }
        return handleOneOffCreateVoucher(voucherInput, eateryId);
    }

    /**
    * This method is invoked only using createVoucher() when isRecurring is true.
    * It is used to handle creating a one-off eatery voucher given voucherInput and eateryId.
    * 
    * @param    voucherInput    A valid VoucherInput object consists of eatingStyle, discount, quantity,
                                    isRecurring, date, startMinute and endMinute.
    * @param    id              A unique id identifying an eatery.
    * @return   A success message.
    * @see      VoucherInput
    * @see      #createVoucher(VoucherInput, String)
    */
    private ResponseEntity<JSONObject> handleRecurringCreateVoucher(VoucherInput voucherInput, Long eateryId) {
        RepeatedVoucher repeatedVoucher = new RepeatedVoucher();
        repeatedVoucher.setId(VoucherUtils.getNextVoucherId(repeatVoucherRepository, voucherRepository));
        repeatedVoucher.setEateryId(eateryId);
        repeatedVoucher.setEatingStyle(voucherInput.getEatingStyle());
        repeatedVoucher.setDiscount(voucherInput.getDiscount());
        repeatedVoucher.setQuantity(voucherInput.getQuantity());
        repeatedVoucher.setDate(voucherInput.getDate());
        repeatedVoucher.setStart(voucherInput.getStartMinute());
        repeatedVoucher.setEnd(voucherInput.getEndMinute());

        repeatedVoucher.setNextUpdate(Date.from(voucherInput.getDate().toInstant().plus(Duration.ofDays(7))));
        repeatedVoucher.setRestockTo(voucherInput.getQuantity());
        repeatedVoucher.setActive(true);

        repeatVoucherRepository.save(repeatedVoucher);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Successfully created recurring voucher"));
    }

    /**
    * This method is invoked only using createVoucher() when isRecurring is false.
    * It is used to handle creating a one-off eatery voucher given voucherInput and eateryId.
    * 
    * @param    voucherInput    A valid VoucherInput object consists of eatingStyle, discount, quantity,
                                    isRecurring, date, startMinute and endMinute.
    * @param    id              A unique id identifying an eatery.
    * @return   A success message.
    * @see      VoucherInput
    * @see      #createVoucher(VoucherInput, String)
    */
    private ResponseEntity<JSONObject> handleOneOffCreateVoucher(VoucherInput voucherInput, Long eateryId) {
        Voucher newVoucher = new Voucher();
        newVoucher.setId(VoucherUtils.getNextVoucherId(repeatVoucherRepository, voucherRepository));
        newVoucher.setEateryId(eateryId);
        newVoucher.setEatingStyle(voucherInput.getEatingStyle());
        newVoucher.setDiscount(voucherInput.getDiscount());
        newVoucher.setQuantity(voucherInput.getQuantity());
        newVoucher.setDate(voucherInput.getDate());
        newVoucher.setStart(voucherInput.getStartMinute());
        newVoucher.setEnd(voucherInput.getEndMinute());
        newVoucher.setActive(true);
        voucherRepository.save(newVoucher);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Successfully created voucher"));
    }

    /**
    * This method is used to edit an existing eatery voucher given a valid eatery token and also
    * a valid voucherInput object with the required fields.
    * @param    voucherInput    A Valid voucherInput object consists of id, eatingStyle, discount, quantity,
                                    isRecurring, date, startMinute and endMinute.
    * @param    token           An authentication token that is unique to an eatery.
    * @return   An appropriate error message or success message.
    * @see      VoucherInput
    */
    public ResponseEntity<JSONObject> editVoucher(VoucherInput voucher, String token) {
        if (voucher == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Please enter the valid information of a voucher"));
        }
        String decodedToken = jwtUtils.decode(token);

        if (decodedToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        if (!eateryRepository.existsByToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is invalid."));
        }

        Long eateryId = Long.valueOf(decodedToken);

        if (!eateryRepository.existsById(eateryId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Eatery does not exist, check your token again"));
        }

        if (!voucher.getEateryId().equals(eateryId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher and eatery does not match, check again"));
        }

        Long voucherId = voucher.getId();

        Optional<Voucher> voucherInDb = voucherRepository.findById(voucherId);
        Optional<RepeatedVoucher> repeatedVoucherInDb = repeatVoucherRepository.findById(voucherId);

        if (!voucherInDb.isPresent() && !repeatedVoucherInDb.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher does not exist, check again"));
        }

        if (voucherInDb.isPresent()) {
            Voucher voucherDb = voucherInDb.get();
            // Convert it to recurring and delete old voucher
            if (voucher.getIsRecurring() != null && voucher.getIsRecurring()) {
                return editRepeatedVoucher(voucher, voucherDb, null);
            } else { // Do normal updates
                return editVoucher(voucher, null, voucherDb);
            }
        }

        RepeatedVoucher repeatedVoucherDb = repeatedVoucherInDb.get();
        // Convert to a one-time voucher and delete old one
        if (voucher.getIsRecurring() != null && !voucher.getIsRecurring()) {
            return editVoucher(voucher, repeatedVoucherDb, null);
        } else { // Edit existing recurring voucher
            return editRepeatedVoucher(voucher, null, repeatedVoucherDb);
        }
    }

    /**
    * This method is invoked by editVoucher to edit a voucher to a repeated voucher given a valid one-off or
    * repeated voucher and the fields to be updated in editVoucher.
    * @param    voucher         A valid VoucherInput object consists of id, eatingStyle, discount, quantity,
                                    isRecurring, date, startMinute and endMinute.
    * @param    oldVoucher      A one-time voucher object to be converted to a repeating voucher and updated with VoucherInput values.
    * @param    existingVoucher A RepeatedVoucher to be updated with VoucherInput values.
    * @return   An appropriate error message or success message.
    * @see      VoucherInput
    * @see      #editVoucher(VoucherInput, String)
    */
    public ResponseEntity<JSONObject> editRepeatedVoucher(VoucherInput voucher, Voucher oldVoucher, RepeatedVoucher existingVoucher) {
        RepeatedVoucher repeatedVoucher = new RepeatedVoucher();
        if (oldVoucher == null && existingVoucher != null) {
            repeatedVoucher.setId(existingVoucher.getId());
        } else {
            repeatedVoucher.setId(oldVoucher.getId());
        }

        repeatedVoucher.setEateryId(voucher.getEateryId());
        if (voucher.getEatingStyle() != null) {
            repeatedVoucher.setEatingStyle(voucher.getEatingStyle());
        } else if (oldVoucher != null) {
            repeatedVoucher.setEatingStyle(oldVoucher.getEatingStyle());
        } else {
            repeatedVoucher.setEatingStyle(existingVoucher.getEatingStyle());
        }

        if (voucher.getQuantity() != null) {
            if (voucher.getQuantity() < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid voucher quantity"));
            }
            repeatedVoucher.setQuantity(voucher.getQuantity());
            repeatedVoucher.setRestockTo(voucher.getQuantity());
        } else if (oldVoucher != null) {
            repeatedVoucher.setQuantity(oldVoucher.getQuantity());
            repeatedVoucher.setRestockTo(oldVoucher.getQuantity());
        } else {
            repeatedVoucher.setQuantity(existingVoucher.getQuantity());
            repeatedVoucher.setRestockTo(existingVoucher.getRestockTo());
        }

        if (voucher.getDiscount() != null) {
            if (voucher.getDiscount() < 0 || voucher.getDiscount() > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid voucher discount"));
            }
            repeatedVoucher.setDiscount(voucher.getDiscount());
        } else if (oldVoucher != null) {
            repeatedVoucher.setDiscount(oldVoucher.getDiscount());
        } else {
            repeatedVoucher.setDiscount(existingVoucher.getDiscount());
        }

        if (voucher.getDate() != null) {
            repeatedVoucher.setDate(voucher.getDate());
        } else if (oldVoucher != null) {
            repeatedVoucher.setDate(oldVoucher.getDate());
        } else {
            repeatedVoucher.setDate(existingVoucher.getDate());
        }

        if (voucher.getStartMinute() != null && voucher.getEndMinute() != null) {
            if ((voucher.getEndMinute() - voucher.getStartMinute()) < MIN_VOUCHER_DURATION || (voucher.getEndMinute() - voucher.getStartMinute()) > MAX_VOUCHER_DURATION) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Vouchers must be at least 30 min long and cannot exceed 24 hours."));
            }

            if (voucher.getStartMinute() < 0 || voucher.getStartMinute() >= 1440 || voucher.getEndMinute() <= 0 || voucher.getStartMinute() >= voucher.getEndMinute()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid times"));
            }

            if (!VoucherUtils.isValidTime(voucher.getDate(), voucher.getEndMinute())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("End time must not be in the past."));
            }

            repeatedVoucher.setStart(voucher.getStartMinute());
            repeatedVoucher.setEnd(voucher.getEndMinute());
            repeatedVoucher.setNextUpdate(Date.from(repeatedVoucher.getDate().toInstant().plus(Duration.ofDays(7))));
        } else if (oldVoucher != null) {
            repeatedVoucher.setStart(oldVoucher.getStart());
            repeatedVoucher.setEnd(oldVoucher.getEnd());
            repeatedVoucher.setNextUpdate(Date.from(oldVoucher.getDate().toInstant().plus(Duration.ofDays(7))));
        } else {
            repeatedVoucher.setStart(existingVoucher.getStart());
            repeatedVoucher.setEnd(existingVoucher.getEnd());
            repeatedVoucher.setNextUpdate(Date.from(existingVoucher.getDate().toInstant().plus(Duration.ofDays(7))));
        }
        if (oldVoucher != null) {
            voucherRepository.deleteById(oldVoucher.getId());
        }

        repeatedVoucher.setActive(true);

        repeatVoucherRepository.save(repeatedVoucher);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Voucher was edited successfully."));
    }

    
    /**
    * This method is invoked by editVoucher to edit a voucher to a one-off voucher given a valid one-off or
    * repeated voucher and the fields to be updated in editVoucher.
    * @param    voucher         A valid VoucherInput object consists of id, eatingStyle, discount, quantity,
                                    isRecurring, date, startMinute and endMinute.
    * @param    oldVoucher      A one-time Voucher object updated to a with VoucherInput values.
    * @param    existingVoucher A RepeatedVoucher to be converted to a one-time voucher and updated with VoucherInput values.
    * @return   An appropriate error message or success message.
    * @see      VoucherInput
    * @see      #editVoucher(VoucherInput, String)
    */
    public ResponseEntity<JSONObject> editVoucher(VoucherInput voucher, RepeatedVoucher oldVoucher, Voucher existingVoucher) {
        Voucher newVoucher = new Voucher();
        if (oldVoucher == null && existingVoucher != null) {
            newVoucher.setId(existingVoucher.getId());
        } else {
            newVoucher.setId(oldVoucher.getId());
        }
        newVoucher.setEateryId(voucher.getEateryId());

        if (voucher.getEatingStyle() != null) {
            newVoucher.setEatingStyle(voucher.getEatingStyle());
        } else if (oldVoucher != null) {
            newVoucher.setEatingStyle(oldVoucher.getEatingStyle());
        } else {
            newVoucher.setEatingStyle(existingVoucher.getEatingStyle());
        }

        if (voucher.getQuantity() != null) {
            if (voucher.getQuantity() < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid voucher quantity"));
            }
            newVoucher.setQuantity(voucher.getQuantity());
        } else if (oldVoucher != null) {
            newVoucher.setQuantity(oldVoucher.getQuantity());
        } else {
            newVoucher.setQuantity(existingVoucher.getQuantity());
        }

        if (voucher.getDiscount() != null) {
            if (voucher.getDiscount() < 0 || voucher.getDiscount() > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid voucher discount"));
            }
            newVoucher.setDiscount(voucher.getDiscount());
        } else if (oldVoucher != null) {
            newVoucher.setDiscount(oldVoucher.getDiscount());
        } else {
            newVoucher.setDiscount(existingVoucher.getDiscount());
        }

        if (voucher.getDate() != null) {
            newVoucher.setDate(voucher.getDate());
        } else if (oldVoucher != null) {
            newVoucher.setDate(oldVoucher.getDate());
        } else {
            newVoucher.setDate(existingVoucher.getDate());
        }

        if (voucher.getStartMinute() != null && voucher.getEndMinute() != null) {
            if ((voucher.getEndMinute() - voucher.getStartMinute()) < MIN_VOUCHER_DURATION || (voucher.getEndMinute() - voucher.getStartMinute()) > MAX_VOUCHER_DURATION) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Vouchers must be at least 30 min long and cannot exceed 24 hours."));
            }

            if (voucher.getStartMinute() < 0 || voucher.getStartMinute() >= 1440 || voucher.getEndMinute() <= 0 || voucher.getStartMinute() >= voucher.getEndMinute()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid times"));
            }

            if (!VoucherUtils.isValidTime(voucher.getDate(), voucher.getEndMinute())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("End time must not be in the past."));
            }

            newVoucher.setStart(voucher.getStartMinute());
            newVoucher.setEnd(voucher.getEndMinute());
        } else if (oldVoucher != null) {
            newVoucher.setStart(oldVoucher.getStart());
            newVoucher.setEnd(oldVoucher.getEnd());
        } else {
            newVoucher.setStart(existingVoucher.getStart());
            newVoucher.setEnd(existingVoucher.getEnd());
        }

        if (oldVoucher != null) {
            repeatVoucherRepository.deleteById(oldVoucher.getId());
        }

        newVoucher.setActive(true);

        voucherRepository.save(newVoucher);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Voucher was edited successfully."));
    }

    /**
    * This method is used to delete a voucher given that the token is from an eatery, the voucher
    * exists and belongs to the eatery.
    * @param    voucherId   A valid id that uniquely identifies a voucher.
    * @param    token       An authentication token that uniquely identifies an eatery.
    * @return   An error message on failure or a success message when successful.
    */
    public ResponseEntity<JSONObject> deleteVoucher(Long voucherId, String token) {
        String decodedToken = jwtUtils.decode(token);

        if (decodedToken == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        Eatery eateryInDb = eateryRepository.findByToken(token);

        if (eateryInDb == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        if (!voucherRepository.existsById(voucherId) && !repeatVoucherRepository.existsById(voucherId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher does not exist"));
        }

        if (repeatVoucherRepository.existsById(voucherId)) {
            RepeatedVoucher repeatedVoucher = repeatVoucherRepository.getById(voucherId);
            if (repeatedVoucher.getEateryId() != eateryInDb.getId())
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Voucher does not belong to eatery."));
            repeatedVoucher.setActive(false);
            repeatedVoucher.setQuantity(0);
            repeatedVoucher.setNextUpdate(null);
            repeatVoucherRepository.save(repeatedVoucher);
        } else if (voucherRepository.existsById(voucherId)) {
            Voucher voucher = voucherRepository.getById(voucherId);
            if (voucher.getEateryId() != eateryInDb.getId())
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Voucher does not belong to eatery."));
            voucher.setActive(false);
            voucher.setQuantity(0);
            voucherRepository.save(voucher);
        }
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Voucher successfully deleted."));
    }

    /**
    * This method is used to verify a voucher given a valid eatery token and code.
    * The code must be unused.
    * 
    * @param    code    A five-digit alphanumeric code that is used to validate a booking.
    * @param    token   An authentication token that uniquely identifies an eatery.
    * @return   An error message on failure or a success response containing a message and booking details when successful.
    */
    public ResponseEntity<JSONObject> verifyVoucher(String code, String token) {
        String decodedToken = jwtUtils.decode(token);
        if (decodedToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        // Check if token is from an eatery
        Eatery eateryInDb = eateryRepository.findByToken(token);

        if (eateryInDb == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        // Check if code is from the eatery
        BookingRecord booking = bookingRecordRepository.findBookingByEateryIdAndCode(eateryInDb.getId(), code);

        if (booking == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid voucher code."));
        }

        boolean voucherExists = voucherRepository.existsById(booking.getVoucherId());
        boolean repeatedVoucherExists = repeatVoucherRepository.existsById(booking.getVoucherId());

        // Check if voucher is in redeem range
        if ((voucherExists || repeatedVoucherExists) && !VoucherUtils.isInTimeRange(booking.getDate(), booking.getStart(), booking.getEnd())) {
            if (VoucherUtils.checkActive(booking.getDate(), booking.getEnd())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher cannot be redeemed yet."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher is expired."));
            }
        } else if (!voucherExists && !repeatedVoucherExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher cannot be found."));
        }

        // Check if voucher is already redeemed.
        if (booking.isRedeemed()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher has already been redeemed."));
        }

        // If voucher is, set booking to redeemed.
        booking.setRedeemed(true);
        bookingRecordRepository.save(booking);

        HashMap<String, Object> dinerBooking = new HashMap<String, Object>();
        dinerBooking.put("eatingStyle", booking.getEatingStyle());
        dinerBooking.put("discount", booking.getDiscount());
        dinerBooking.put("date", booking.getDate());
        dinerBooking.put("start", booking.getStart());
        dinerBooking.put("end", booking.getEnd());

        Map<String, Object> dataMedium = new HashMap<>();
        dataMedium.put("data", dinerBooking);
        JSONObject data = new JSONObject(dataMedium);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Voucher is valid and has been used!", data));
    }
}
