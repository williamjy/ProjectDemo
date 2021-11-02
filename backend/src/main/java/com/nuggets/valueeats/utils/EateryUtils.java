package com.nuggets.valueeats.utils;

import com.nuggets.valueeats.entity.Eatery;
import com.nuggets.valueeats.repository.voucher.RepeatVoucherRepository;
import com.nuggets.valueeats.repository.voucher.VoucherRepository;

import java.util.HashMap;

public class EateryUtils {

    /**
    * This utility method is used for creating a HashMap containing eatery details.
    * 
    * @param    voucherRepository       A repository storing one-off vouchers.
    * @param    repeatVoucherRepository A repository storing repeated vouchers.
    * @param    e                       An Eatery object containing eatery details.
    * @param    distanceFromDiner       A HashMap containing a diner's distance from each eatery address.
    * @return   A HashMap of the eatery details.
    */
    public static HashMap<String, Object> createEatery(VoucherRepository voucherRepository, RepeatVoucherRepository repeatVoucherRepository, Eatery e, HashMap<String, Integer> distanceFromDiner) {
        HashMap<String, Object> eatery = new HashMap<>();

        Long maxDiscountVoucher = voucherRepository.findMaxDiscountFromEatery(e.getId());
        Long maxDiscountRepeatVoucher = repeatVoucherRepository.findMaxDiscountFromEatery(e.getId());
        long maxDiscount = 0;

        if (maxDiscountVoucher != null && maxDiscountRepeatVoucher != null) {
            maxDiscount = Math.max(maxDiscountVoucher, maxDiscountRepeatVoucher);
        } else if (maxDiscountVoucher != null) {
            maxDiscount = maxDiscountVoucher;
        } else if (maxDiscountRepeatVoucher != null) {
            maxDiscount = maxDiscountRepeatVoucher;
        }

        eatery.put("name", e.getAlias());
        eatery.put("discount", maxDiscount + "%");
        if (e.getLazyRating() != null) {
            eatery.put("rating", String.format("%.1f", e.getLazyRating()));
        } else {
            eatery.put("rating", "0.0");
        }
        eatery.put("id", e.getId());
        eatery.put("profilePic", e.getProfilePic());
        eatery.put("cuisines", e.getCuisines());

        if (distanceFromDiner != null && distanceFromDiner.containsKey(e.getAddress())) {
            if (distanceFromDiner.get(e.getAddress()) != Integer.MAX_VALUE) {
                eatery.put("distance", DistanceUtils.convertDistanceToString(distanceFromDiner.get(e.getAddress())));
            } else {
                eatery.put("distance", "N/A");
            }
        }

        return eatery;
    }
}
