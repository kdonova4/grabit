package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.CouponRepository;
import com.kdonova4.grabit.enums.DiscountType;
import com.kdonova4.grabit.model.entity.Coupon;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository repository;

    public CouponService(CouponRepository repository) {
        this.repository = repository;
    }

    public List<Coupon> findAll() {
        return repository.findAll();
    }

    public List<Coupon> findByDiscountType(DiscountType type) {
        return repository.findByDiscountType(type);
    }

    public Optional<Coupon> findByCouponCode(String code) {
        return repository.findByCouponCode(code);
    }

    public List<Coupon> findByIsActive(boolean isActive) {
        return repository.findByIsActive(isActive);
    }

    public Optional<Coupon> findById(int id) {
        return repository.findById(id);
    }

    public Result<Coupon> create(Coupon coupon) {
        Result<Coupon> result = validate(coupon);

        if(!result.isSuccess())
            return result;

        if(coupon.getCouponId() != 0) {
            result.addMessages("CouponId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        coupon = repository.save(coupon);
        result.setPayload(coupon);
        return result;
    }

    public boolean deleteById(int id) {
        if(repository.findById(id).isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private Result<Coupon> validate(Coupon coupon) {
        Result<Coupon> result = new Result<>();

        if(coupon == null) {
            result.addMessages("COUPON CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(coupon.getCouponCode() == null || coupon.getCouponCode().isBlank() || coupon.getCouponCode().length() != 16) {
            result.addMessages("COUPON CODE MUST BE 16 CHARACTERS AND NOT NULL OR BLANK", ResultType.INVALID);
        }

        if(coupon.getDiscount() <= 0) {
            result.addMessages("DISCOUNT MUST BE GREATER THAN 0", ResultType.INVALID);
        }

        if(coupon.getDiscountType() == null) {
            result.addMessages("DISCOUNT TYPE IS REQUIRED", ResultType.INVALID);
        } else if(coupon.getDiscountType() == DiscountType.PERCENTAGE && coupon.getDiscount() > 100) {
            result.addMessages("DISCOUNT CANNOT BE GREATER THAN 100 WHEN DISCOUNT TYPE IS PERCENTAGE", ResultType.INVALID);
        }


        if(coupon.getExpireDate() == null) {
            result.addMessages("EXPIRE DATE IS REQUIRED", ResultType.INVALID);
        } else if(coupon.getExpireDate().isBefore(LocalDateTime.now())) {
            result.addMessages("EXPIRE DATE MUST BE IN THE FUTURE", ResultType.INVALID);
        }

        if(repository.findByCouponCode(coupon.getCouponCode()).isPresent()) {
            result.addMessages("COUPON CODE ALREADY BELONGS TO ANOTHER COUPON", ResultType.INVALID);
        }

        return result;
    }
}
