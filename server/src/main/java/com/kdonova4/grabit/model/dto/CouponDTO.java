package com.kdonova4.grabit.model.dto;

import com.kdonova4.grabit.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {
    private int couponId;
    private String couponCode;
    private int discount;
    private DiscountType discountType;
}
