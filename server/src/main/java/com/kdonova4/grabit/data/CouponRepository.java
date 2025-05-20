package com.kdonova4.grabit.data;

import com.kdonova4.grabit.enums.DiscountType;
import com.kdonova4.grabit.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    List<Coupon> findByDiscountType(DiscountType type);

    List<Coupon> findByCouponCode(String couponCode);

    List<Coupon> findByIsActive(boolean isActive);
}
