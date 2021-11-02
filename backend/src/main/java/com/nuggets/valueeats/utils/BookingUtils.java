package com.nuggets.valueeats.utils;

import com.nuggets.valueeats.entity.BookingRecord;
import com.nuggets.valueeats.entity.voucher.RepeatedVoucher;
import com.nuggets.valueeats.entity.voucher.Voucher;
import com.nuggets.valueeats.entity.voucher.VoucherEatingStyle;
import com.nuggets.valueeats.repository.BookingRecordRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class BookingUtils {

    /**
    * This utility method is used for setting voucher details to a booking record.
    * 
    * @param    bookingRecord   A BookingRecord object.
    * @param    voucher         A Voucher object that consists of eatingStyle, discount, date, start
    *                               and end.
    * @return   A bookingRecord with set values.
    * @see      Voucher
    */
    public static BookingRecord setVoucherDetails(BookingRecord bookingRecord, Voucher voucher) {
        bookingRecord.setEatingStyle(voucher.getEatingStyle());
        bookingRecord.setDiscount(voucher.getDiscount());
        bookingRecord.setDate(voucher.getDate());
        bookingRecord.setStart(voucher.getStart());
        bookingRecord.setEnd(voucher.getEnd());
        return bookingRecord;
    }

    /**
    * This utility method is used for setting Repeated Voucher details to a booking record.
    * 
    * @param    bookingRecord   A BookingRecord object.
    * @param    voucher         A RepeatedVoucher object that consists of eatingStyle, discount, date, start
    *                               and end.
    * @return   A bookingRecord with set values.
    * @see      RepeatedVoucher
    */
    public static BookingRecord setVoucherDetails(BookingRecord bookingRecord, RepeatedVoucher repeatedVoucher) {
        bookingRecord.setEatingStyle(repeatedVoucher.getEatingStyle());
        bookingRecord.setDiscount(repeatedVoucher.getDiscount());
        bookingRecord.setDate(repeatedVoucher.getDate());
        bookingRecord.setStart(repeatedVoucher.getStart());
        bookingRecord.setEnd(repeatedVoucher.getEnd());
        return bookingRecord;
    }

    /**
    * This utility method is used for generating a unique 5 digit alphanumeric code.
    * 
    * @param    bookingRecordRepository   BookingRecordRepository containing booking records.
    * @return   A string containing a unique 5 digit alphanumeric code.
    */
    public static String generateRandomCode(BookingRecordRepository bookingRecordRepository) {
        String uuid = UUID.randomUUID().toString().substring(0, 5);
        while (bookingRecordRepository.existsByCode(uuid)) {
            uuid = UUID.randomUUID().toString().substring(0, 5);
        }
        return uuid;
    }

    /**
    * This utility method is used for creating a HashMap containing booking details.
    * 
    * @param    id          An id that uniquely identifies a booking.
    * @param    code        A unique code that is used for verifying a booking.
    * @param    date        A date that defines the voucher active date
    * @param    start       An integer that defines the start minute.
    * @param    end         An integer that defines the end minute.
    * @param    eatingStyle A VoucherEatingStyle that defines an eating style.
    * @param    discount    A double that defines the voucher discount.
    * @param    eateryId    An id that uniquely identifies the eatery.
    * @param    isRedeemed  A boolean that defines whether or not the voucher has been redeemed.
    * @param    alias       A string that defines the eatery name.
    * @return   A HashMap of the booking details.
    * @see      VoucherEatingStyle
    */
    public static HashMap<String, Object> createBooking(Long id, String code, Date date, Integer start, Integer end, VoucherEatingStyle eatingStyle, Double discount, Long eateryId, boolean isRedeemed, String alias) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String strDate = formatter.format(date);
        int startHour = start / 60; //since both are ints, you get an int
        int startMinute = start % 60;
        int endHour = end / 60; //since both are ints, you get an int
        int endMinute = end % 60;

        HashMap<String, Object> dinerBooking = new HashMap<>();

        dinerBooking.put("bookingId", id);
        dinerBooking.put("code", code);
        dinerBooking.put("isActive", VoucherUtils.checkActive(date, end));
        dinerBooking.put("eatingStyle", eatingStyle);
        dinerBooking.put("discount", discount);
        dinerBooking.put("eateryId", eateryId);
        dinerBooking.put("isRedeemable", VoucherUtils.isInTimeRange(date, start, end));
        dinerBooking.put("used", isRedeemed);
        dinerBooking.put("eateryName", alias);
        dinerBooking.put("date", strDate);
        dinerBooking.put("startTime", String.format("%d:%02d", startHour, startMinute));
        dinerBooking.put("endTime", String.format("%d:%02d", endHour, endMinute));

        return dinerBooking;
    }
}
