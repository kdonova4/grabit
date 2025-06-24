package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.domain.*;
import com.kdonova4.grabit.domain.mapper.OrderMapper;
import com.kdonova4.grabit.enums.*;
import com.kdonova4.grabit.model.*;
import com.kdonova4.grabit.security.AppUserService;
import com.kdonova4.grabit.security.JwtConverter;
import com.kdonova4.grabit.security.SecurityConfig;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerTest {

    @MockBean
    OrderService service;

    @MockBean
    AppUserService appUserService;

    @MockBean
    AddressService addressService;

    @MockBean
    JwtConverter jwtConverter;

    @Autowired
    MockMvc mockMvc;

    String token;

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private AppUser user;
    private AppRole role;
    private Address address;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        role = new AppRole(1, "SELLER", Set.of(user));
        user.setRoles(Set.of(role));

        when(appUserService.findByUsername("kdonova4")).thenReturn(Optional.of(user));

        address = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);


        // mock token return value
        when(jwtConverter.getTokenFromUser(any())).thenReturn("fake-jwt-token");
        token = jwtConverter.getTokenFromUser(user);

        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        var request = get("/api/v1/orders");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByUserShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/orders/user/1");
        Order order = new Order(1, user, Timestamp.valueOf(LocalDateTime.now()), address, address, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());
        when(appUserService.findUserById(1)).thenReturn(Optional.of(user));
        when(service.findByUser(any(AppUser.class))).thenReturn(List.of(order));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByUserShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/orders/user/1");
        when(appUserService.findUserById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByAddressShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/orders/shipping-address/1");
        Order order = new Order(1, user, Timestamp.valueOf(LocalDateTime.now()), address, address, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());
        when(addressService.findById(1)).thenReturn(Optional.of(address));
        when(service.findByShippingAddress(any(Address.class))).thenReturn(List.of(order));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByAddressShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/orders/shipping-address/1");
        when(addressService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {
        Order order = new Order(1, user, Timestamp.valueOf(LocalDateTime.now()), address, address, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());

        when(service.findById(1)).thenReturn(Optional.of(order));

        String orderJson = jsonMapper.writeValueAsString(OrderMapper.toResponse(order));

        var request = get("/api/v1/orders/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(orderJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(service.findById(1)).thenReturn(Optional.empty());

        var request = get("/api/v1/orders/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }



}
