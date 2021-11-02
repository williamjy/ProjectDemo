package com.nuggets.valueeats.service;

import com.nuggets.valueeats.entity.Diner;
import com.nuggets.valueeats.entity.Eatery;
import com.nuggets.valueeats.entity.Review;
import com.nuggets.valueeats.entity.User;
import com.nuggets.valueeats.entity.voucher.RepeatedVoucher;
import com.nuggets.valueeats.entity.voucher.Voucher;
import com.nuggets.valueeats.repository.*;
import com.nuggets.valueeats.repository.voucher.RepeatVoucherRepository;
import com.nuggets.valueeats.repository.voucher.VoucherRepository;
import com.nuggets.valueeats.utils.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserManagementService {
    @Autowired
    private UserRepository<User> userRepository;
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
    private BookingRecordRepository bookingRecordRepository;
    @Autowired
    private JwtUtils jwtUtils;

    /**
    * This method is used to register an eatery given a valid Eatery object with the required fields.
    * 
    * @param    eatery  An Eatery object that contains eatery details.
    *                   Must contain alias, email and password. Can contain profilePic, address, cuisines
                        and menuPhotos.
    * @return   An error message on failure or a success message when successful.
    */
    @Transactional
    public ResponseEntity<JSONObject> registerEatery(Eatery eatery) {
        ResponseEntity<JSONObject> result = register(eatery);
        if (result.getStatusCode().is2xxSuccessful()) {
            eateryRepository.save(eatery);
        }

        return result;
    }

    /**
    * This method is used to register an diner given a valid Diner object with the required fields.
    * 
    * @param    diner  A Diner object that contains eatery details.
    *                   Must contain alias, email and password. Can contain profilePic and address.
    * @return   An error message on failure or a success message when successful.
    */
    @Transactional
    public ResponseEntity<JSONObject> registerDiner(Diner diner) {

        ResponseEntity<JSONObject> result = register(diner);
        if (result.getStatusCode().is2xxSuccessful()) {
            dinerRepository.save(diner);
        }

        return result;
    }

    /**
    * This method is invoked by {@linkplain #registerDiner(Diner)} or {@linkplain #registerEatery(Eatery)}.
    * 
    * @param    user  A User object that contains User details. Must contain alias, email and password.
    * @return   An error message on failure or a success message when successful.
    */
    @Transactional
    public ResponseEntity<JSONObject> register(User user) {
        if (user.getEmail() == null || user.getPassword() == null || user.getAlias() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Missing fields"));
        }
        user.setEmail(user.getEmail().toLowerCase());

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseUtils.createResponse("Email is taken, try another"));
        }

        String result = validInputChecker(user);
        if (result != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse(result));
        }

        user.setId(userRepository.findMaxId() == null ? 0 : userRepository.findMaxId() + 1);
        user.setPassword(EncryptionUtils.encrypt(user.getPassword(), String.valueOf(user.getId())));
        String userToken = jwtUtils.encode(String.valueOf(user.getId()));
        user.setToken(userToken);

        Map<String, String> dataMedium = new HashMap<>();
        dataMedium.put("token", userToken);
        JSONObject data = new JSONObject(dataMedium);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Welcome to ValueEats, " + user.getAlias(), data));
    }

    /**
    * This method is used for logging in a user given valid User details.
    * 
    * @param    user  A User object that contains User details. Must contain email and password.
    * @return   An error message on failure or a success message when successful.
    */
    @Transactional
    public ResponseEntity<JSONObject> login(User user) {
        User userDb;
        try {
            user.setEmail(user.getEmail().toLowerCase());
            userDb = userRepository.findByEmail(user.getEmail());
        } catch (PersistenceException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse(e.toString()));
        }

        if (userDb == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Failed to login, please try again"));
        }

        String token = jwtUtils.encode(String.valueOf(userDb.getId()));
        userDb.setToken(token);

        userRepository.save(userDb);

        Map<String, String> dataMedium = new HashMap<>();
        dataMedium.put("token", token);

        return AuthenticationUtils.loginPasswordCheck(user.getPassword(), String.valueOf(userDb.getId()),
                userDb.getPassword(), "Welcome back, " + userDb.getEmail(),
                dinerRepository.existsByEmail(userDb.getEmail()), dataMedium);
    }

    /**
    * This method is used for logging out a user given a token.
    * 
    * @param    token  An authentication token that identifies a user.
    * @return   An error message on failure or a success message when successful.
    */
    @Transactional
    public ResponseEntity<JSONObject> logout(String token) {
        if (!userRepository.existsByToken(token) || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Can't find the token: " + token));
        }

        String userId = jwtUtils.decode(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Can't get user associated with token"));
        }

        User user = userRepository.findByToken(token);
        user.setToken("");
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Logout was successful"));
    }

    /**
    * This method is used for updating a diner's detail given a token and Diner details.
    * 
    * @param    token  An authentication token that identifies a diner.
    * @param    diner  A Diner object that contains the Diner details to be updated.
    * @return   An error message on failure or a success message when successful.
    */
    @Transactional
    public ResponseEntity<JSONObject> updateDiner(Diner diner, String token) {
        ResponseEntity<JSONObject> result = update(diner, token);
        if (result.getStatusCode().is2xxSuccessful()) {
            diner.setToken(token);
            dinerRepository.save(diner);
        }

        return result;
    }

    
    /**
    * This method is used for updating a eatery's detail given a token and Eatery details.
    * 
    * @param    token  An authentication token that identifies a eatery.
    * @param    eatery  A Eatery object that contains the Eatery details to be updated.
    * @return   An error message on failure or a success message when successful.
    */
    @Transactional
    public ResponseEntity<JSONObject> updateEatery(Eatery eatery, String token) {
        ResponseEntity<JSONObject> result = update(eatery, token);
        if (result.getStatusCode().is2xxSuccessful()) {

            Eatery eateryDb = eateryRepository.findByToken(token);

            if (eatery.getCuisines() == null) {
                eatery.setCuisines(eateryDb.getCuisines());
            }
            if (eatery.getMenuPhotos() == null) {
                eatery.setMenuPhotos(eateryDb.getMenuPhotos());
            }
            eatery.setToken(token);
            eateryRepository.save(eatery);
        }

        return result;
    }

    /**
    * This method is invoked by {@linkplain #updateDiner(Diner, String)} or {@linkplain #updateEatery(Eatery, String)} 
    * to update a user's detail given a token and User details to be updated.
    * 
    * @param    token  An authentication token that identifies a user.
    * @param    user  A User object that contains the User details to be updated.
    * @see #updateDiner(Diner, String)
    * @see #updateEatery(Eatery, String)
    * @return   An error message on failure or a success message when successful.
    */
    @Transactional
    public ResponseEntity<JSONObject> update(User user, String token) {
        User userDb;
        try {
            userDb = userRepository.findByToken(token);
        } catch (PersistenceException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse(e.toString()));
        }

        if (userDb == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Failed to verify, please try again"));
        }
        String result = processNewProfile(user, userDb);

        if (result != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse(result));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse("Update profile successfully, "
                + user.getAlias()));
    }

    /**
    * This method is used for validating email and password.
    * 
    * @param    user  A User object that contains an email and password.
    * @return   An error message on failure or a success message when successful.
    */
    public String validInputChecker(final User user) {
        if (!ValidationUtils.isValidEmail(user.getEmail())) {
            return "Invalid Email Format.";
        }
        if (!ValidationUtils.isValidPassword(user.getPassword())) {
            return "Password must be between 8 to 32 characters long, and contain a lower and uppercase character.";
        }
        if (!ValidationUtils.isValidAlias(user.getAlias())) {
            return "Please enter a username with at most 12 characters.";
        }
        return null;
    }

    /**
    * This method is used for updating a user profile with new details.
    * 
    * @param    newProfile  A User object that contains the new profile information.
    * @param    oldProfile  A User object that contains the old profile information.
    * @return   An error message on failure or a success message when successful.
    */
    public String processNewProfile(User newProfile, User oldProfile) {

        newProfile.setId(oldProfile.getId());

        if (newProfile.getEmail() != null) {
            if (!ValidationUtils.isValidEmail(newProfile.getEmail())) {
                return "Invalid Email Format.";
            }
            if (userRepository.existsByEmail(newProfile.getEmail())) {
                if (!oldProfile.getEmail().equals(newProfile.getEmail().toLowerCase())) {
                    return "Email is taken, try another";
                }
            }
            newProfile.setEmail(newProfile.getEmail().toLowerCase());
        } else {
            newProfile.setEmail(oldProfile.getEmail());
        }

        if (newProfile.getPassword() != null) {
            String newPassword = EncryptionUtils.encrypt(newProfile.getPassword(), String.valueOf(newProfile.getId()));
            if (!ValidationUtils.isValidPassword(newProfile.getPassword())) {
                return "Password must be between 8 to 32 characters long, and contain a lower and uppercase character.";
            }
            newProfile.setPassword(newPassword);
        } else {
            newProfile.setPassword(oldProfile.getPassword());
        }

        if (newProfile.getAlias() != null) {
            // Error checking for new alias if needed
        } else {
            newProfile.setAlias(oldProfile.getAlias());
        }

        if (newProfile.getAddress() != null) {
            // Error checking for new address if needed
        } else {
            newProfile.setAddress(oldProfile.getAddress());
        }

        if (newProfile.getProfilePic() != null) {
            // Error checking for new profile pic if needed.
        } else {
            newProfile.setProfilePic(oldProfile.getProfilePic());
        }
        return null;
    }

    /**
    * This method is used for obtaining the details of a diner.
    * 
    * @param    token  An authentication token that is unique to a diner.
    * @return   An error message on failure or a success message when successful.
    */
    public ResponseEntity<JSONObject> getDinerProfile(String token) {
        String decodedToken = jwtUtils.decode(token);

        if (decodedToken == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        if (!dinerRepository.existsByToken(token) || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is invalid"));
        }

        Diner diner = dinerRepository.findByToken(token);
        if (diner == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Diner does not exist"));
        }

        List<Review> reviews = reviewRepository.findByDinerId(diner.getId());
        ArrayList<Object> reviewsList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("name", diner.getAlias());
        result.put("email", diner.getEmail());
        result.put("profile picture", diner.getProfilePic());
        for (Review r : reviews) {
            Optional<Eatery> db = eateryRepository.findById(r.getEateryId());
            if (!db.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Eatery does not exist"));
            }

            Eatery e = db.get();
            HashMap<String, Object> review = ReviewUtils.createReview(r.getId(), diner.getProfilePic(), diner.getAlias(),
                    r.getMessage(), r.getRating(), r.getEateryId(), r.getReviewPhotos(), e.getAlias());
            reviewsList.add(review);
        }
        result.put("reviews", reviewsList);

        return ResponseEntity.status(HttpStatus.OK).body(new JSONObject(result));
    }

    /**
    * This method is used for obtaining the details of a eatery.
    * 
    * @param    token  An authentication token that is unique to a user.
    * @return   An error message on failure or a success message when successful.
    */
    public ResponseEntity<JSONObject> getEateryProfile(Long id, String token) {
        String decodedToken = jwtUtils.decode(token);
        if (decodedToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is not valid or expired"));
        }

        Eatery eateryDb;
        Diner dinerDb = null;
        if (eateryRepository.existsByToken(token) && !token.isEmpty()) {
            eateryDb = eateryRepository.findByToken(token);
        } else {
            if (token.isEmpty() || !(dinerRepository.existsByToken(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("Token is invalid"));
            }
            if (id == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtils.createResponse("ID is required"));
            }
            Optional<Eatery> eateryInDb = eateryRepository.findById(id);
            if (!eateryInDb.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Eatery does not exist"));
            }

            eateryDb = eateryInDb.get();
            dinerDb = dinerRepository.findByToken(token);
        }

        List<Review> reviews = reviewRepository.listReviewsOfEatery(eateryDb.getId());
        ArrayList<Object> reviewsList = new ArrayList<>();
        for (Review r : reviews) {
            Long reviewDinerId = r.getDinerId();

            Optional<Diner> reviewerInDinerDb = dinerRepository.findById(reviewDinerId);

            if (!reviewerInDinerDb.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtils.createResponse("Eatery does not exist"));
            }

            Diner reviewDinerDb = reviewerInDinerDb.get();

            HashMap<String, Object> review = ReviewUtils.createReview(r.getId(), reviewDinerDb.getProfilePic(), reviewDinerDb.getAlias(),
                    r.getMessage(), r.getRating(), r.getEateryId(), r.getReviewPhotos(), eateryDb.getAlias());
            if (dinerDb != null) {
                review.put("isOwner", dinerDb.getId().equals(reviewDinerDb.getId()));
            }
            reviewsList.add(review);
        }

        ArrayList<RepeatedVoucher> repeatVouchersList = repeatVoucherRepository.findByEateryId(eateryDb.getId());
        ArrayList<Voucher> vouchersList = voucherRepository.findActiveByEateryId(eateryDb.getId());
        ArrayList<Object> combinedVoucherList = new ArrayList<>();
        for (RepeatedVoucher v : repeatVouchersList) {
            HashMap<String, Object> voucher = VoucherUtils.createVoucher(v.getId(), v.getDiscount(), v.getEateryId(), v.getEatingStyle(),
                    v.getQuantity(), v.getDate(), v.getStart(), v.getEnd(), dinerDb, true,
                    v.getNextUpdate(), bookingRecordRepository);
            combinedVoucherList.add(voucher);
        }

        for (Voucher v : vouchersList) {
            HashMap<String, Object> voucher = VoucherUtils.createVoucher(v.getId(), v.getDiscount(), v.getEateryId(), v.getEatingStyle(),
                    v.getQuantity(), v.getDate(), v.getStart(), v.getEnd(), dinerDb, false,
                    null, bookingRecordRepository);
            combinedVoucherList.add(voucher);
        }

        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("id", eateryDb.getId());
        map.put("name", eateryDb.getAlias());
        map.put("email", eateryDb.getEmail());
        map.put("profilePic", eateryDb.getProfilePic());
        if (eateryDb.getLazyRating() != null) {
            map.put("rating", String.format("%.1f", eateryDb.getLazyRating()));
        } else {
            map.put("rating", "0.0");
        }
        map.put("address", eateryDb.getAddress());
        map.put("menuPhotos", eateryDb.getMenuPhotos());
        map.put("reviews", reviewsList);
        map.put("cuisines", eateryDb.getCuisines());
        map.put("vouchers", combinedVoucherList);

        JSONObject data = new JSONObject(map);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.createResponse(data));
    }
}
