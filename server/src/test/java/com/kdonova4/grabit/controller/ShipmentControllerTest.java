package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.data.ShipmentRepository;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.enums.ShipmentStatus;
import com.kdonova4.grabit.model.dto.ShipmentResponseDTO;
import com.kdonova4.grabit.model.entity.AppRole;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.Shipment;
import com.kdonova4.grabit.security.JwtConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShipmentControllerTest {

    @MockBean
    ShipmentRepository repository;

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
    private Shipment shipment;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        role = new AppRole(1, "SELLER", Set.of(user));
        user.setRoles(Set.of(role));

        order = new Order(1, user, Timestamp.valueOf(LocalDateTime.now()), null, null, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());
        shipment = new Shipment(1, order, ShipmentStatus.PENDING, "TRACKTRACKTRACK123", Timestamp.valueOf(LocalDateTime.now()), null);


        when(appUserRepository.findByUsername("kdonova4")).thenReturn(Optional.of(user));
        token = jwtConverter.getTokenFromUser(user);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        var request = get("/api/v1/shipments");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByOrderShouldReturn200() throws Exception {
        var request = get("/api/v1/shipments/order/1");

        Shipment shipment = new Shipment(1, order, ShipmentStatus.PENDING, "TRACKTRACKTRACK123", Timestamp.valueOf(LocalDateTime.now()), null);


        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(repository.findByOrder(any(Order.class))).thenReturn(Optional.of(shipment));

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
    void findByTrackingNumberShouldReturn200() throws Exception {
        var request = get("/api/v1/shipments/tracking/TRACKTRACKTRACK123");

        Shipment shipment = new Shipment(1, order, ShipmentStatus.PENDING, "TRACKTRACKTRACK123", Timestamp.valueOf(LocalDateTime.now()), null);


        when(repository.findByTrackingNumber("TRACKTRACKTRACK123")).thenReturn(Optional.of(shipment));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByTrackingNumberShouldReturn404() throws Exception {
        var request = get("/api/v1/shipments/tracking/TRACKTRACKTRACK123");

        when(repository.findByTrackingNumber("TRACKTRACKTRACK123")).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        ShipmentResponseDTO shipmentResponseDTO = new ShipmentResponseDTO(1, order.getOrderId(), ShipmentStatus.PENDING, "TRACKTRACKTRACK123", time, null);
        shipment.setShippedAt(time);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(repository.findById(1)).thenReturn(Optional.of(shipment));

        String shipJson = jsonMapper.writeValueAsString(shipmentResponseDTO);

        var request = get("/api/v1/shipments/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(shipJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());


        var request = get("/api/v1/shipments/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}
