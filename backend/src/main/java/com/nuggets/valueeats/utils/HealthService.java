package com.nuggets.valueeats.utils;

import com.nuggets.valueeats.entity.Diner;
import com.nuggets.valueeats.entity.Eatery;
import com.nuggets.valueeats.entity.Review;
import com.nuggets.valueeats.entity.User;
import com.nuggets.valueeats.entity.voucher.RepeatedVoucher;
import com.nuggets.valueeats.entity.voucher.Voucher;
import com.nuggets.valueeats.repository.DinerRepository;
import com.nuggets.valueeats.repository.EateryRepository;
import com.nuggets.valueeats.repository.ReviewRepository;
import com.nuggets.valueeats.repository.UserRepository;
import com.nuggets.valueeats.repository.voucher.RepeatVoucherRepository;
import com.nuggets.valueeats.repository.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class HealthService {
    @Autowired
    private DinerRepository dinerRepository;
    @Autowired
    private EateryRepository eateryRepository;
    @Autowired
    private UserRepository<User> userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RepeatVoucherRepository repeatVoucherRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    public List<Diner> listDiner() {
        return dinerRepository.findAll();
    }

    public List<Eatery> listEatery() {
        return eateryRepository.findAll();
    }

    public List<User> listUser() {
        return userRepository.findAll();
    }

    public List<Review> listReview() {
        return reviewRepository.findAll();
    }

    public List<Object> listCuisines() {
        return eateryRepository.findAllCuisines();
    }

    public List<RepeatedVoucher> listRepeatVoucher() {
        return repeatVoucherRepository.findAll();
    }

    public List<Voucher> listVoucher() {
        return voucherRepository.findAll();
    }
}
