package com.nuggets.valueeats.entity;

import com.nuggets.valueeats.entity.voucher.VoucherEatingStyle;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data
@PrimaryKeyJoinColumn(name = "id")
public class BookingRecord {
    @Id
    private Long id;
    private Long dinerId;
    private Long eateryId;
    private Long voucherId;
    private String code;
    private boolean redeemed;
    private VoucherEatingStyle eatingStyle;
    private Double discount;
    private Date date;
    private Integer start;
    private Integer end;
}
