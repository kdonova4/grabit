package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.CouponRepository;
import com.kdonova4.grabit.enums.DiscountType;
import com.kdonova4.grabit.model.Coupon;
import com.kdonova4.grabit.model.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    CouponRepository couponRepository;

    @InjectMocks
    CouponService service;

    private Coupon coupon;

    @BeforeEach
    void setup() {
        coupon = new Coupon(1, "SAVESAVESAVERN10", 15, DiscountType.PERCENTAGE, LocalDateTime.now().plusDays(7), true);
    }

    @Test
    void shouldFindAll() {
        when(couponRepository.findAll()).thenReturn(
                List.of(
                        coupon
                )
        );

        List<Coupon> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(couponRepository).findAll();
    }

    @Test
    void shouldFindByDiscountType() {
        when(couponRepository.findByDiscountType(DiscountType.PERCENTAGE)).thenReturn(List.of(coupon));

        List<Coupon> actual = service.findByDiscountType(DiscountType.PERCENTAGE);

        assertEquals(1, actual.size());
        verify(couponRepository).findByDiscountType(DiscountType.PERCENTAGE);
    }

    @Test
    void shouldFindByCouponCode() {
        when(couponRepository.findByCouponCode(coupon.getCouponCode())).thenReturn(Optional.of(coupon));

        Optional<Coupon> actual = service.findByCouponCode(coupon.getCouponCode());

        assertTrue(actual.isPresent());
        verify(couponRepository).findByCouponCode(coupon.getCouponCode());
    }

    @Test
    void shouldFindByIsActive() {
        when(couponRepository.findByIsActive(coupon.isActive())).thenReturn(List.of(coupon));

        List<Coupon> actual = service.findByIsActive(coupon.isActive());

        assertEquals(1, actual.size());
        verify(couponRepository).findByIsActive(coupon.isActive());
    }

    @Test
    void ShouldFindById() {
        when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));
        Optional<Coupon> actual = service.findById(coupon.getCouponId());
        assertTrue(actual.isPresent());
    }

    @Test
    void shouldCreateValid() {
        Coupon mockOut = coupon;
        mockOut.setCouponId(1);
        coupon.setCouponId(0);

        when(couponRepository.save(coupon)).thenReturn(mockOut);
        when(couponRepository.findByCouponCode(coupon.getCouponCode())).thenReturn(Optional.empty());

        Result<Coupon> actual = service.create(coupon);
        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotCreateValid() {

        when(couponRepository.findByCouponCode(coupon.getCouponCode())).thenReturn(Optional.of(coupon));

        Result<Coupon> actual = service.create(coupon);
        assertEquals(ResultType.INVALID, actual.getType());

        coupon.setCouponId(0);
        coupon.setCouponCode(null);
        actual = service.create(coupon);
        assertEquals(ResultType.INVALID, actual.getType());

        coupon.setCouponCode("SAVESAVESAVERN10");
        coupon.setDiscount(-5);
        actual = service.create(coupon);
        assertEquals(ResultType.INVALID, actual.getType());

        coupon.setDiscount(150);
        actual = service.create(coupon);
        assertEquals(ResultType.INVALID, actual.getType());

        coupon.setDiscount(15);
        coupon.setDiscountType(null);
        actual = service.create(coupon);
        assertEquals(ResultType.INVALID, actual.getType());

        coupon.setDiscountType(DiscountType.PERCENTAGE);
        Coupon temp = coupon;
        coupon = null;
        actual = service.create(coupon);
        assertEquals(ResultType.INVALID, actual.getType());

        coupon = temp;
        actual = service.create(coupon);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldDeleteById() {
        when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));
        doNothing().when(couponRepository).deleteById(coupon.getCouponId());
        assertTrue(service.deleteById(coupon.getCouponId()));
        verify(couponRepository).deleteById(coupon.getCouponId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(couponRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(couponRepository, never()).deleteById(anyInt());
    }
}
