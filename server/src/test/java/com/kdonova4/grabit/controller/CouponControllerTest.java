package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.CouponRepository;
import com.kdonova4.grabit.enums.DiscountType;
import com.kdonova4.grabit.model.AppRole;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Category;
import com.kdonova4.grabit.model.Coupon;
import com.kdonova4.grabit.security.JwtConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerTest {

    @MockBean
    CouponRepository repository;

    @MockBean
    AppUserRepository appUserRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtConverter jwtConverter;

    String token;

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private AppUser user;
    private AppRole role;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        role = new AppRole(1, "SELLER", Set.of(user));
        user.setRoles(Set.of(role));

        when(appUserRepository.findByUsername("kdonova4")).thenReturn(Optional.of(user));
        token = jwtConverter.getTokenFromUser(user);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        var request = get("/api/v1/coupons");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByCouponCodeShouldReturn200() throws Exception {
        Coupon coupon = new Coupon(1, "savesavesavern10", 15, DiscountType.PERCENTAGE, LocalDateTime.now().plusDays(7), true);

        when(repository.findByCouponCode(coupon.getCouponCode())).thenReturn(Optional.of(coupon));

        String couponJson = jsonMapper.writeValueAsString(coupon);

        var request = get("/api/v1/coupons/coupon/savesavesavern10");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(couponJson));

    }

    @Test
    void findByMissingCouponCodeShouldReturn404() throws Exception {

        when(repository.findByCouponCode("savesavesavern10")).thenReturn(Optional.empty());


        var request = get("/api/v1/coupons/coupon/savesavesavern10");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIsActiveShouldReturn200() throws Exception {
        Coupon coupon = new Coupon(1, "savesavesavern10", 15, DiscountType.PERCENTAGE, LocalDateTime.now().plusDays(7), true);

        when(repository.findByIsActive(coupon.isActive())).thenReturn(List.of(coupon));

        String couponJson = jsonMapper.writeValueAsString(List.of(coupon));

        var request = get("/api/v1/coupons/active/" + coupon.isActive());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(couponJson));
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {
        Coupon coupon = new Coupon(1, "savesavesavern10", 15, DiscountType.PERCENTAGE, LocalDateTime.now().plusDays(7), true);

        when(repository.findById(1)).thenReturn(Optional.of(coupon));

        String couponJson = jsonMapper.writeValueAsString(coupon);

        var request = get("/api/v1/coupons/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(couponJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var request = get("/api/v1/coupons/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenInvalid() throws Exception {
        Coupon coupon = new Coupon();

        String couponJson = jsonMapper.writeValueAsString(coupon);

        var request = post("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(couponJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn415WhenMultipart() throws Exception {
        Coupon coupon = new Coupon();

        String couponJson = jsonMapper.writeValueAsString(coupon);

        var request = post("/api/v1/coupons")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(couponJson);

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createShouldReturn201() throws Exception {
        Coupon coupon = new Coupon(0, "savesavesavern10", 15, DiscountType.PERCENTAGE, LocalDateTime.now().plusDays(7), true);
        Coupon expected = new Coupon(1, "savesavesavern10", 15, DiscountType.PERCENTAGE, LocalDateTime.now().plusDays(7), true);

        when(repository.save(any(Coupon.class))).thenReturn(expected);

        String couponJson = jsonMapper.writeValueAsString(coupon);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(couponJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void deleteShouldReturn204NoContent() throws Exception {
        doNothing().when(repository).deleteById(1);

        var request = delete("/api/v1/coupons/1")
                .header("Authorization", "Bearer " + token);


        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
