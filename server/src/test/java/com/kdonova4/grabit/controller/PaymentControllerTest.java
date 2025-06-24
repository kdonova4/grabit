package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.data.PaymentRepository;
import com.kdonova4.grabit.domain.mapper.PaymentMapper;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.*;
import com.kdonova4.grabit.security.JwtConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @MockBean
    PaymentRepository repository;

    @MockBean
    OrderRepository orderRepository;

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
    private Order order;
    private Payment payment;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        role = new AppRole(1, "SELLER", Set.of(user));
        user.setRoles(Set.of(role));

        order = new Order(1, user, Timestamp.valueOf(LocalDateTime.now()), null, null, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());

        payment = new Payment(1, order, new BigDecimal(1200), Timestamp.valueOf(LocalDateTime.now()));

        when(appUserRepository.findByUsername("kdonova4")).thenReturn(Optional.of(user));
        token = jwtConverter.getTokenFromUser(user);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        var request = get("/api/v1/payments");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByOrderShouldReturn200() throws Exception {
        var request = get("/api/v1/payments/order/1");

        Payment payment = new Payment(1, order, new BigDecimal(1200), Timestamp.valueOf(LocalDateTime.now()));


        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(repository.findByOrder(any(Order.class))).thenReturn(Optional.of(payment));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByOrderShouldReturn404() throws Exception {
        var request = get("/api/v1/payments/order/1");

        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByAmountPaidGreaterThanShouldReturn200() throws Exception {
        var request = get("/api/v1/payments/amount/greater-than/500");

        Payment payment = new Payment(1, order, new BigDecimal(1200), Timestamp.valueOf(LocalDateTime.now()));

        when(repository.findByAmountPaidGreaterThan(any(BigDecimal.class))).thenReturn(List.of(payment));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByAmountPaidLessThanShouldReturn200() throws Exception {
        var request = get("/api/v1/payments/amount/greater-than/2000");

        Payment payment = new Payment(1, order, new BigDecimal(1200), Timestamp.valueOf(LocalDateTime.now()));

        when(repository.findByAmountPaidGreaterThan(any(BigDecimal.class))).thenReturn(List.of(payment));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {
        Payment payment = new Payment(1, order, new BigDecimal(1200), Timestamp.valueOf(LocalDateTime.now()));

        when(repository.findById(1)).thenReturn(Optional.of(payment));

        String paymentJson = jsonMapper.writeValueAsString(PaymentMapper.toResponse(payment));

        var request = get("/api/v1/payments/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(paymentJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());


        var request = get("/api/v1/payments/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

}
