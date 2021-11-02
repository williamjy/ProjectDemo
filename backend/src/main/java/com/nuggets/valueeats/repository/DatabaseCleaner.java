package com.nuggets.valueeats.repository;

import com.nuggets.valueeats.entity.voucher.RepeatedVoucher;
import com.nuggets.valueeats.entity.voucher.Voucher;
import com.nuggets.valueeats.repository.voucher.RepeatVoucherRepository;
import com.nuggets.valueeats.repository.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class DatabaseCleaner {
    @Autowired
    private RepeatVoucherRepository repeatVoucherRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    @Scheduled(cron = "0 * * * * *")
    public void updateRepeatedVoucher() {
        List<RepeatedVoucher> repeatedVouchers = repeatVoucherRepository.findAll();

        Date timeNow = new Date(System.currentTimeMillis());
        timeNow = Date.from(timeNow.toInstant().plus(Duration.ofHours(10)));

        if (repeatedVouchers != null) {
            for (RepeatedVoucher repeatedVoucher : repeatedVouchers) {
                if (repeatedVoucher.getNextUpdate() != null && repeatedVoucher.getNextUpdate().compareTo(timeNow) < 0) {

                    repeatedVoucher.setQuantity(repeatedVoucher.getRestockTo());
                    repeatedVoucher.setActive(true);
                    repeatedVoucher.setDate(repeatedVoucher.getNextUpdate());
                    repeatedVoucher.setNextUpdate(Date.from(repeatedVoucher.getNextUpdate().toInstant().plus(Duration.ofDays(7))));
                    repeatVoucherRepository.save(repeatedVoucher);
                }
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void updateExpiredVoucher() {
        List<RepeatedVoucher> repeatedVouchers = repeatVoucherRepository.findAllActive();
        List<Voucher> vouchers = voucherRepository.findAllActive();

        Date timeNow = new Date(System.currentTimeMillis());
        timeNow = Date.from(timeNow.toInstant().plus(Duration.ofHours(10)));

        if (repeatedVouchers != null) {
            for (RepeatedVoucher repeatedVoucher : repeatedVouchers) {
                Date endTime = Date.from(repeatedVoucher.getDate().toInstant().plus(Duration.ofMinutes(repeatedVoucher.getEnd())));

                if (endTime.compareTo(timeNow) < 0) {

                    repeatedVoucher.setActive(false);
                    repeatVoucherRepository.save(repeatedVoucher);
                }
            }
        }
        if (vouchers != null) {
            for (Voucher voucher : vouchers) {
                Date endTime = Date.from(voucher.getDate().toInstant().plus(Duration.ofMinutes(voucher.getEnd())));

                if (endTime.compareTo(timeNow) < 0) {

                    voucher.setActive(false);
                    voucherRepository.save(voucher);
                }
            }
        }
    }
}
