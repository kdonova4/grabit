package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.CouponService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.enums.DiscountType;
import com.kdonova4.grabit.model.Category;
import com.kdonova4.grabit.model.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Coupon Controller", description = "Coupon Operations")
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService service;


    public CouponController(CouponService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Finds All Coupons")
    public ResponseEntity<List<Coupon>> findAll() {
        List<Coupon> coupons = service.findAll();

        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Finds A Coupon Using The Code Associated With The Coupon")
    public ResponseEntity<Coupon> findByCouponCode(@PathVariable String code) {
        Optional<Coupon> coupon = service.findByCouponCode(code);

        if(coupon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(coupon.get());
    }

    @GetMapping("/active/{isActive}")
    @Operation(summary = "Finds Coupons That Are Still Active")
    public ResponseEntity<List<Coupon>> findByIsActive(@PathVariable boolean isActive) {
        List<Coupon> coupons = service.findByIsActive(isActive);

        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{couponId}")
    @Operation(summary = "Finds A Coupon By ID")
    public ResponseEntity<Coupon> findById(@PathVariable int couponId) {
        Optional<Coupon> coupon = service.findById(couponId);

        if(coupon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(coupon.get());
    }

    @PostMapping
    @Operation(summary = "Creates A Coupon")
    public ResponseEntity<Object> create(@RequestBody Coupon coupon) {
        Result<Coupon> result = service.create(coupon);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{couponId}")
    @Operation(summary = "Deletes A Coupon")
    public ResponseEntity<Object> deleteById(@PathVariable int couponId) {
        service.deleteById(couponId);
        return ResponseEntity.noContent().build();
    }

}
