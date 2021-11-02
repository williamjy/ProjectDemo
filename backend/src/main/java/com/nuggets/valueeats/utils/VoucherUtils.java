package com.nuggets.valueeats.utils;

import com.nuggets.valueeats.entity.Diner;
import com.nuggets.valueeats.entity.voucher.VoucherEatingStyle;
import com.nuggets.valueeats.repository.BookingRecordRepository;
import com.nuggets.valueeats.repository.voucher.RepeatVoucherRepository;
import com.nuggets.valueeats.repository.voucher.VoucherRepository;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;

public class VoucherUtils {

    /**
    * This utility method is used to get the duration between now and a given time.
    * 
    * @param    date  A Date object containing the date to be compared to.
    * @param    end   An integer containing the end minute of the date.
    * @return   A long value of the duration between now and a given time.
    */
    public static Long getDuration(Date date, Integer end) {
        Date endTime = Date.from(date.toInstant().plus(Duration.ofMinutes(end)));
        Date timeNow = new Date(System.currentTimeMillis());
        timeNow = Date.from(timeNow.toInstant().plus(Duration.ofHours(10)));

        return Duration.between(timeNow.toInstant(), endTime.toInstant()).toMillis();
    }

    /**
    * This utility method is used to check if the voucher is active.
    * 
    * @param    date  A Date object containing the voucher date.
    * @param    end   An integer containing the end minute of the voucher.
    * @return   A boolean of whether a voucher is active.
    */
    public static boolean checkActive(Date date, Integer end) {
        Date timeNow = new Date(System.currentTimeMillis());
        timeNow = Date.from(timeNow.toInstant().plus(Duration.ofHours(10)));
        Date endTime = Date.from(date.toInstant().plus(Duration.ofMinutes(end)));
        return (endTime.compareTo(timeNow) > 0);
    }

    /**
    * This utility method is used to check if the voucher is in time range.
    * 
    * @param    date  A Date object containing the voucher date.
    * @param    start   An integer containing the start minute of the voucher.
    * @param    end   An integer containing the end minute of the voucher.
    * @return   A boolean of whether a voucher is redeemable.
    */
    public static boolean isInTimeRange(Date date, Integer start, Integer end) {
        Date timeNow = new Date(System.currentTimeMillis());
        timeNow = Date.from(timeNow.toInstant().plus(Duration.ofHours(10)));
        Date startTime = Date.from(date.toInstant().plus(Duration.ofMinutes(start)));
        Date endTime = Date.from(date.toInstant().plus(Duration.ofMinutes(end)));
        // Check if start time before timeNow, endTime after timeNow
        return startTime.compareTo(timeNow) <= 0 && endTime.compareTo(timeNow) > 0;
    }

    /**
    * This utility method is used to check if a time is in the future.
    * 
    * @param    date  A Date object containing a date.
    * @param    minutes   An integer containing the minute of the date.
    * @return   A boolean of whether a time is in the future.
    */
    public static boolean isValidTime(Date date, Integer minutes) {
        Date time = new Date();
        time = Date.from(date.toInstant().plus(Duration.ofMinutes(minutes)));
        Date timeNow = new Date(System.currentTimeMillis());
        timeNow = Date.from(timeNow.toInstant().plus(Duration.ofHours(10)));

        return (time.compareTo(timeNow) > 0);
    }

    /**
    * This utility method is used for creating a HashMap containing voucher details.
    * 
    * @param    id                      An id that uniquely identifies a voucher.
    * @param    discount                A double containing voucher discount.
    * @param    eateryId                A long containing eatery id.
    * @param    voucherEatingStyle      A VoucherEatingStyle containing the eating style of the voucher.
    * @param    quantity                An integer containing the quantity of vouchers remaining.
    * @param    date                    A Date containing the voucher date.
    * @param    startTime               An integer containing the voucher start minute.
    * @param    endTime                 An integer containing the voucher end minute.
    * @param    dinerDb                 A Diner object containing diner details.
    * @param    isRecurring             A boolean of whether the voucher is recurring.
    * @param    getNextUpdate           A Date of when a repeated voucher should be updated.
    * @param    bookingRecordRepository A repository containing booking records.
    * @return   A HashMap of the voucher details.
    * @see      VoucherEatingStyle
    */
    public static HashMap<String, Object> createVoucher(Long id, Double discount, Long eateryId, VoucherEatingStyle voucherEatingStyle,
            Integer quantity, Date date, Integer startTime, Integer endTime, Diner dinerDb,
            boolean isRecurring, Date getNextUpdate, BookingRecordRepository bookingRecordRepository) {

        HashMap<String, Object> voucher = new HashMap<String, Object>();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String strDate = formatter.format(date);
        int startHour = startTime / 60;
        int startMinute = startTime % 60;
        int endHour = endTime / 60;
        int endMinute = endTime % 60;

        voucher.put("id", id);
        voucher.put("discount", discount);
        voucher.put("eateryId", eateryId);
        voucher.put("eatingStyle", voucherEatingStyle);
        voucher.put("quantity", quantity);
        voucher.put("duration", getDuration(date, endTime));
        voucher.put("isActive", checkActive(date, endTime));
        voucher.put("isRedeemable", isInTimeRange(date, startTime, endTime));
        voucher.put("date", strDate);
        voucher.put("startTime", String.format("%d:%02d", startHour, startMinute));
        voucher.put("endTime", String.format("%d:%02d", endHour, endMinute));
        voucher.put("isRecurring", isRecurring);

        if (dinerDb != null) {
            voucher.put("disableButton", (bookingRecordRepository.existsByDinerIdAndVoucherId(dinerDb.getId(), id)) != 0);
        } else {
            voucher.put("disableButton", true);
        }

        if (getNextUpdate != null) {
            String nextUpdate = formatter.format(getNextUpdate);
            voucher.put("nextUpdate", nextUpdate);
        } else {
            voucher.put("nextUpdate", "Deleted");
        }

        return voucher;
    }

    /**
    * This utility method is used to get the next available id in the database.
    * 
    * @param    repeatVoucherRepository     A repository that stores RepeatedVoucher.
    * @param    voucherRepository           A repository that stores Voucher.
    * @return   An unused voucher id.
    */
    public static Long getNextVoucherId(RepeatVoucherRepository repeatVoucherRepository, VoucherRepository voucherRepository) {
        long newId;
        if (repeatVoucherRepository.findMaxId() != null && voucherRepository.findMaxId() != null) {
            newId = Math.max((repeatVoucherRepository.findMaxId() + 1), (voucherRepository.findMaxId() + 1));
        } else if (repeatVoucherRepository.findMaxId() != null) {
            newId = repeatVoucherRepository.findMaxId() + 1;
        } else if (voucherRepository.findMaxId() != null) {
            newId = voucherRepository.findMaxId() + 1;
        } else {
            newId = (long) 0;
        }
        return newId;
    }
}
