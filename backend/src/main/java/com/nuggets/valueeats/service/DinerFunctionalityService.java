package com.nuggets.valueeats.service;

import com.nuggets.valueeats.entity.BookingRecord;
import com.nuggets.valueeats.entity.Diner;
import com.nuggets.valueeats.entity.Eatery;
import com.nuggets.valueeats.entity.Review;
import com.nuggets.valueeats.entity.voucher.RepeatedVoucher;
import com.nuggets.valueeats.entity.voucher.Voucher;
import com.nuggets.valueeats.repository.BookingRecordRepository;
import com.nuggets.valueeats.repository.DinerRepository;
import com.nuggets.valueeats.repository.EateryRepository;
import com.nuggets.valueeats.repository.ReviewRepository;
import com.nuggets.valueeats.repository.voucher.RepeatVoucherRepository;
import com.nuggets.valueeats.repository.voucher.VoucherRepository;
import com.nuggets.valueeats.utils.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DinerFunctionalityService {
    @Autowired
    private DinerRepository dinerRepository;
    @Autowired
    private EateryRepository eateryRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private RepeatVoucherRepository repeatVoucherRepository;
    @Autowired
    private BookingRecordRepository bookingRepository;
    @Autowired
    private DistanceUtils distanceUtils;
    @Autowired
    private BookingRecordRepository bookingRecordRepository;
    @Autowired
    private JwtUtils jwtUtils;

    /**
    * This method is used for creating a diner review for an eatery using the Review details.
    * 
    * @param    review  A Review object that must contain a rating, eateryId and optional message and reviewPhotos.
    * @param    token  An authentication token that is unique to a diner.
    * @return   An error message on failure or a success message when successful.
    */
    public ResponseEntity<JSONObject> createReview(Review review, String token) {
        try {
            // Check if token is valid
            if (!dinerRepository.existsByToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Invalid token"));
            }

            // Check for required inputs
            if (!(token != null && review.getEateryId() != null)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Missing fields"));
            }

            // Check for required inputs
            if (review.getRating() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Review must have a rating."));
            }

            // Check if eatery id exists
            if (!eateryRepository.existsById(review.getEateryId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid Eatery ID"));
            }

            // Check if rating is between 0.5 to 5 and is in increments of 0.5
            if (!ReviewUtils.isValidRating(review.getRating())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Rating must be between 0.5 and 5 and in 0.5 increments"));
            }

            // Check if review character length does not exceed 280 characters.
            if (!ReviewUtils.isValidMessage(review.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Message must not exceed 280 characters"));
            }

            Long dinerId = dinerRepository.findByToken(token).getId();

            if (bookingRepository.existsByDinerIdAndEateryId(dinerId, review.getEateryId()) == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("You cannot review a restaurant that you have not dined at."));
            }

            // Check if user already made a review
            if (reviewRepository.existsByDinerIdAndEateryId(dinerId, review.getEateryId()) == 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("User already has an existing review for this eatery."));
            }

            review.setDinerId(dinerId);
            review.setId(reviewRepository.findMaxId() == null ? 0 : reviewRepository.findMaxId() + 1);

            reviewRepository.save(review);

            Map<String, Long> dataMedium = new HashMap<>();
            dataMedium.put("reviewId", review.getId());
            JSONObject data = new JSONObject(dataMedium);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Review was created successfully", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.createResponse(e.toString()));
        }
    }

    /**
    * This method is used for removing a diner review for an eatery given the Review details.
    * 
    * @param    review  A Review object that must contain a id and eateryId.
    * @param    token  An authentication token that is unique to a diner.
    * @return   An error message on failure or a success message when successful.
    */
    public ResponseEntity<JSONObject> removeReview(Review review, String token) {
        try {
            // Check if token is valid
            if (!dinerRepository.existsByToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Invalid diner token"));
            }

            // Check for required inputs
            if (!(token != null && review.getEateryId() != null && review.getId() != null)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Missing fields"));
            }

            // Check if eatery id exists
            if (!eateryRepository.existsById(review.getEateryId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid Eatery ID"));
            }

            Long dinerId = dinerRepository.findByToken(token).getId();

            // Check if diner has a review and delete it
            if (reviewRepository.existsByDinerIdAndEateryIdAndReviewId(dinerId, review.getEateryId(), review.getId()) == 1) {
                reviewRepository.deleteById(review.getId());
                return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Review was deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Review does not exist."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.createResponse(e.toString()));
        }
    }

    /**
    * This method is used for acquiring a list of eateries sorted in a specific order.
    * If no sorting option or invalid sorting option is given, eateries are sorted by ID.
    * 
    * @param    token  An authentication token that is unique to a diner.
    * @param    sort  A string which is either "Distance", "New", or "Rating" to determine eatery sorting.
    * @param    latitude  The latitude of the diner's location if using distance sorting.
    * @param    longitude  The longitude of the diner's location if using distance sorting.
    * @return   An error response with error message or a success response with list of eateries.
    */
    public ResponseEntity<JSONObject> listEateries(String token, String sort, Double latitude, Double longitude) {
        if (!dinerRepository.existsByToken(token) || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Invalid token"));
        }

        Diner diner = dinerRepository.findByToken(token);
        List<Eatery> eateryList = eateryRepository.findAll();
        HashMap<String, Integer> distanceFromDiner = null;
        if ("New".equals(sort)) {
            eateryList = eateryRepository.findAllByOrderByIdDesc();
        } else if ("Rating".equals(sort)) {
            eateryList = eateryRepository.findAllByOrderByLazyRatingDesc();
        } else if ("Distance".equals(sort)) {
            if (latitude == null || longitude == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Location must be provided."));
            }
            if (eateryList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("There are no eateries at the moment."));
            }

            List<String> addresses = new ArrayList<>();
            for (Eatery e : eateryList) {
                addresses.add(e.getAddress());
            }

            try {
                String addressesURLString = URLEncoder.encode(String.join("|", addresses), "UTF-8");
                distanceFromDiner = distanceUtils.findDistanceFromDiner(latitude, longitude, addressesURLString, addresses);
                if (distanceFromDiner == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Unable to retrieve distance data at the moment."));
                }

                final HashMap<String, Integer> distanceFromDinerFinal = distanceFromDiner;
                final PriorityQueue<AbstractMap.SimpleImmutableEntry<Integer, Eatery>> pq = eateryList.stream()
                        .map(a -> new AbstractMap.SimpleImmutableEntry<>(distanceFromDinerFinal.get(a.getAddress()), a))
                        .collect(Collectors.toCollection(() -> new PriorityQueue<>((a, b) -> b.getKey() - a.getKey())));

                List<Eatery> result = new ArrayList<>();
                while (!pq.isEmpty()) {
                    Eatery newEatery = pq.poll().getValue();
                    result.add(0, newEatery);
                }

                eateryList = result;
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Address cannot be encoded."));
            }
        }

        ArrayList<Object> list = new ArrayList<>();
        for (Eatery e : eateryList) {
            HashMap<String, Object> map = EateryUtils.createEatery(voucherRepository, repeatVoucherRepository, e, distanceFromDiner);
            list.add(map);
        }

        Map<String, Object> dataMedium = new HashMap<>();
        dataMedium.put("name", diner.getAlias());
        dataMedium.put("eateryList", list);
        JSONObject data = new JSONObject(dataMedium);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse(data));
    }

    /**
    * This method is used for editing a diner review using the new Review details.
    * 
    * @param    review  A Review object that must contain the id, eateryId and rating, and optional message and reviewPhotos.
    * @param    token   An authentication token that is unique to a diner.
    * @return   An error message on failure or success message when successful.
    */
    public ResponseEntity<JSONObject> editReview(Review review, String token) {
        try {
            // Check if token is valid
            if (!dinerRepository.existsByToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Invalid token"));
            }

            // Check for required inputs
            if (!(token != null && review.getEateryId() != null && review.getId() != null)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Missing fields"));
            }

            // Check for required inputs
            if (review.getRating() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Review must have a rating."));
            }

            // Check if eatery id exists
            if (!eateryRepository.existsById(review.getEateryId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Invalid Eatery ID"));
            }

            Long dinerId = dinerRepository.findByToken(token).getId();

            // Check if review exists and is made by the diner for the specific eatery
            if (reviewRepository.existsByDinerIdAndEateryIdAndReviewId(dinerId, review.getEateryId(), review.getId()) == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Matching review does not exist"));
            }

            Optional<Review> reviewInDb = reviewRepository.findById(review.getId());
            if (!reviewInDb.isPresent()) {

            }
            Review reviewDb = reviewInDb.get();

            // Check if new review character length does not exceed 280 characters.
            if (review.getMessage() != "") {
                if (!ReviewUtils.isValidMessage(review.getMessage())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Message must not exceed 280 characters"));
                }
                reviewDb.setMessage(review.getMessage());
            }

            // Check if new rating is between 0.5 to 5 and is in increments of 0.5
            if (review.getRating() != null) {
                if (!ReviewUtils.isValidRating(review.getRating())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Rating must be between 0.5 and 5 and in 0.5 increments"));
                }
                reviewDb.setRating(review.getRating());
            }

            if (review.getReviewPhotos() != null) {
                reviewDb.setReviewPhotos(review.getReviewPhotos());
            }

            reviewRepository.save(reviewDb);

            Map<String, Long> dataMedium = new HashMap<>();
            dataMedium.put("reviewId", review.getId());
            JSONObject data = new JSONObject(dataMedium);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Review was edited successfully", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.createResponse(e.toString()));
        }
    }

    /**
    * This method is used for listing the vouchers of a diner given a valid diner token.
    * 
    * @param    token   An authentication token that is unique to a diner.
    * @return   An error message on failure or success response containing the diner's voucher details.
    */
    public ResponseEntity<JSONObject> dinerListVouchers(String token) {
        String id = jwtUtils.decode(token);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }
        Long dinerId;
        try {
            dinerId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Invalid ID format"));
        }

        if (!dinerRepository.existsById(dinerId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Diner does not exist, check your token please"));
        }

        ArrayList<Object> bookingData = new ArrayList<>();
        ArrayList<BookingRecord>  dinerBookings = bookingRecordRepository.findAllByDinerId(dinerId);

        for (BookingRecord booking : dinerBookings) {
            boolean voucherExists = voucherRepository.existsById(booking.getVoucherId());
            boolean repeatVoucherExists = repeatVoucherRepository.existsById(booking.getVoucherId());

            if (voucherExists || repeatVoucherExists) {
                Optional<Eatery> eatery = eateryRepository.findById(booking.getEateryId());
                if (!eatery.isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Eatery ID is booking is invalid."));
                }
                Eatery eateryDb = eatery.get();

                HashMap<String, Object> dinerBooking = BookingUtils.createBooking(booking.getId(), booking.getCode(), booking.getDate(), booking.getStart(),
                        booking.getEnd(), booking.getEatingStyle(), booking.getDiscount(), booking.getEateryId(),
                        booking.isRedeemed(), eateryDb.getAlias());

                if (VoucherUtils.isInTimeRange(booking.getDate(), booking.getStart(), booking.getEnd())) {
                    dinerBooking.put("duration", VoucherUtils.getDuration(booking.getDate(), booking.getEnd()));
                }

                bookingData.add(dinerBooking);
            }
        }

        Map<String, Object> dataMedium = new HashMap<>();
        dataMedium.put("vouchers", bookingData);
        JSONObject data = new JSONObject(dataMedium);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse(data));
    }

    /**
    * This method is used for booking an eatery voucher given a valid diner token and voucherId.
    * 
    * @param    voucherId   An id that is unique to a voucher.
    * @param    token       An authentication token that is unique to a diner.
    * @return   An error message on failure or success response containing the diner's booking details.
    */
    public ResponseEntity<JSONObject> bookVoucher(Long voucherId, String token) {
        String decodedToken = jwtUtils.decode(token);
        if (decodedToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        Diner dinerInDb = dinerRepository.findByToken(token);
        if (dinerInDb == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        Long dinerId = dinerInDb.getId();
        if (!voucherRepository.existsById(voucherId) && !repeatVoucherRepository.existsById(voucherId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher does not exist"));
        }

        if (bookingRecordRepository.existsByDinerIdAndVoucherId(dinerId, voucherId) != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("You cannot book more than one of the same voucher."));
        }

        BookingRecord bookingRecord = new BookingRecord();
        if (repeatVoucherRepository.existsById(voucherId)) {
            RepeatedVoucher repeatedVoucher = repeatVoucherRepository.getById(voucherId);

            if (!repeatedVoucher.isActive()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher is no longer active."));
            }

            if (repeatedVoucher.getQuantity() < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("No enough voucher for booking"));
            } else {
                repeatedVoucher.setQuantity(repeatedVoucher.getQuantity() - 1);
                repeatVoucherRepository.save(repeatedVoucher);
            }

            BookingUtils.setVoucherDetails(bookingRecord, repeatedVoucher);
            bookingRecord.setId(bookingRecordRepository.findMaxId() == null ? 0 : bookingRecordRepository.findMaxId() + 1);
            bookingRecord.setDinerId(dinerId);
            bookingRecord.setEateryId(repeatedVoucher.getEateryId());
            bookingRecord.setCode(BookingUtils.generateRandomCode(bookingRecordRepository));
            bookingRecord.setVoucherId(voucherId);
        } else if (voucherRepository.existsById(voucherId)) {
            Voucher voucher = voucherRepository.getById(voucherId);

            if (!voucher.isActive()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Voucher is no longer active."));
            }

            if (voucher.getQuantity() < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("No enough voucher for booking"));
            } else {
                voucher.setQuantity(voucher.getQuantity() - 1);
                voucherRepository.save(voucher);
            }

            if (voucher.getQuantity() == 0) {
                voucher.setActive(false);
            }

            BookingUtils.setVoucherDetails(bookingRecord, voucher);
            bookingRecord.setId(bookingRecordRepository.findMaxId() == null ? 0 : bookingRecordRepository.findMaxId() + 1);
            bookingRecord.setDinerId(dinerId);
            bookingRecord.setEateryId(voucher.getEateryId());
            bookingRecord.setCode(BookingUtils.generateRandomCode(bookingRecordRepository));
            bookingRecord.setVoucherId(voucherId);
        }

        bookingRecord.setRedeemed(false);
        bookingRecordRepository.save(bookingRecord);

        Map<String, String> dataMedium = new HashMap<>();

        dataMedium.put("id", String.valueOf(bookingRecord.getId()));
        dataMedium.put("dinerId", String.valueOf(bookingRecord.getDinerId()));
        dataMedium.put("eateryId", String.valueOf(bookingRecord.getEateryId()));
        dataMedium.put("code", bookingRecord.getCode());

        JSONObject data = new JSONObject(dataMedium);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Successfully booked", data));
    }
}


