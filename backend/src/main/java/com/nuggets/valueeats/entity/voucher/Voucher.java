package com.nuggets.valueeats.entity.voucher;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
public class Voucher {
    @Id
    private Long id;
    private Long eateryId;
    private VoucherEatingStyle eatingStyle;
    private Double discount;
    private Integer quantity;
    private Date date;
    private Integer start;
    private Integer end;
    private boolean isActive;
}
