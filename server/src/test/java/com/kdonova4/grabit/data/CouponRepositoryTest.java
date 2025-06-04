package com.kdonova4.grabit.data;
import com.kdonova4.grabit.enums.DiscountType;
import com.kdonova4.grabit.model.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class CouponRepositoryTest {

    @Autowired
    CouponRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByDiscountType() {
        List<Coupon> coupons = repository.findByDiscountType(DiscountType.PERCENTAGE);

        assertEquals(1, coupons.size());
    }

    @Test
    void shouldFindByCouponCode() {
        Optional<Coupon> coupon = repository.findByCouponCode("SAVE10");

        assertNotNull(coupon.get());
    }

    @Test
    void shouldFindByIsActive() {
        List<Coupon> coupons = repository.findByIsActive(true);

        assertEquals(1, coupons.size());
    }

    @Test
    void shouldCreate() {
        Coupon coupon = new Coupon(0, "MENARDS", 15, DiscountType.PERCENTAGE, LocalDateTime.now().plusDays(7), true);
        repository.save(coupon);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);

        assertEquals(0, repository.findAll().size());
    }
}
