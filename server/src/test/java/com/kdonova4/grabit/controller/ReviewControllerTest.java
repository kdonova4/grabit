package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.*;
import com.kdonova4.grabit.enums.*;
import com.kdonova4.grabit.model.*;
import com.kdonova4.grabit.security.JwtConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @MockBean
    ReviewRepository repository;

    @MockBean
    AppUserRepository appUserRepository;

    @MockBean
    ProductRepository productRepository;

    @MockBean
    OrderRepository orderRepository;

    @MockBean
    ShipmentRepository shipmentRepository;

    @MockBean
    OrderProductRepository orderProductRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtConverter jwtConverter;

    String token;

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private AppUser user;
    private AppUser seller;
    private AppRole buyerRole;
    private AppRole sellerRole;
    private Product product;
    private Order order;
    private OrderProduct orderProduct;
    private Shipment shipment;
    private Review review;
    private Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        seller = new AppUser(2, "dono2223", "dono2223@gmail.com", "85c*98Kd", false, new HashSet<>());
        buyerRole = new AppRole(1, "BUYER", Set.of(user));
        sellerRole = new AppRole(1, "SELLER", Set.of(user));
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);
        order = new Order(1, user, Timestamp.valueOf(LocalDateTime.now()), null, null, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());
        orderProduct = new OrderProduct(1, order, product, 1, new BigDecimal(1200), new BigDecimal(1200));
        shipment = new Shipment(1, order, ShipmentStatus.DELIVERED, "TRACKTRACKTRACK123", Timestamp.valueOf(LocalDateTime.now()), null);

        user.setRoles(Set.of(buyerRole));
        seller.setRoles(Set.of(sellerRole));
        review = new Review(1, 4, "Great seller, cant wait!", user, seller, product, timestamp);


        when(appUserRepository.findByUsername("kdonova4")).thenReturn(Optional.of(user));
        token = jwtConverter.getTokenFromUser(user);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        var request = get("/api/v1/reviews");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByPostedByShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/reviews/user/1");
        Review review = new Review(1, 4, "Great seller, cant wait!", user, seller, product, Timestamp.valueOf(LocalDateTime.now()));

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(repository.findByPostedBy(any(AppUser.class))).thenReturn(List.of(review));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByPostedByShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/reviews/user/1");

        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByProductShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/reviews/product/1");
        Review review = new Review(1, 4, "Great seller, cant wait!", user, seller, product, Timestamp.valueOf(LocalDateTime.now()));

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(repository.findByProduct(any(Product.class))).thenReturn(List.of(review));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByProductShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/reviews/user/1/product/1");

        when(productRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findBySellerShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/reviews/seller/2");
        Review review = new Review(1, 4, "Great seller, cant wait!", user, seller, product, Timestamp.valueOf(LocalDateTime.now()));

        when(appUserRepository.findById(2)).thenReturn(Optional.of(seller));
        when(repository.findByPostedBy(any(AppUser.class))).thenReturn(List.of(review));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findBySellerShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/reviews/seller/2");

        when(appUserRepository.findById(2)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByPostedByAndSellerShouldReturn200() throws Exception {
        var request = get("/api/v1/reviews/user/1/seller/2");
        Review review = new Review(1, 4, "Great seller, cant wait!", user, seller, product, Timestamp.valueOf(LocalDateTime.now()));

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.findById(2)).thenReturn(Optional.of(seller));
        when(repository.findByPostedByAndSeller(any(AppUser.class), any(AppUser.class))).thenReturn(List.of(review));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByPostedByAndSellerShouldReturn404() throws Exception {
        var request = get("/api/v1/reviews/user/1/seller/2");
        Review review = new Review(1, 4, "Great seller, cant wait!", user, seller, product, Timestamp.valueOf(LocalDateTime.now()));

        when(appUserRepository.findById(1)).thenReturn(Optional.empty());
        when(appUserRepository.findById(2)).thenReturn(Optional.of(seller));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByPostedByAndProductShouldReturn200() throws Exception {
        var request = get("/api/v1/reviews/user/1/product/1");
        Review review = new Review(1, 4, "Great seller, cant wait!", user, seller, product, Timestamp.valueOf(LocalDateTime.now()));

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(repository.findByPostedByAndProduct(any(AppUser.class), any(Product.class))).thenReturn(Optional.of(review));


        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByPostedByAndProductShouldReturn404() throws Exception {
        var request = get("/api/v1/reviews/user/1/product/1");

        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));


        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {

        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO(1, 4, "Great seller, cant wait!", user.getAppUserId(), seller.getAppUserId(), product.getProductId(), timestamp);
        when(repository.findById(1)).thenReturn(Optional.of(review));

        String reviewJson = jsonMapper.writeValueAsString(reviewResponseDTO);

        var request = get("/api/v1/reviews/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(reviewJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var request = get("/api/v1/reviews/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenInvalid() throws Exception {
        Review review = new Review();

        String reviewJson = jsonMapper.writeValueAsString(review);

        var request = post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn415WhenMultipart() throws Exception {
        Review review = new Review();

        String reviewJson = jsonMapper.writeValueAsString(review);

        var request = post("/api/v1/watchlists")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createShouldReturn201() throws Exception {
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(4, "Great seller, cant wait!", user.getAppUserId(), seller.getAppUserId(), product.getProductId());
        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO(1, 4, "Great seller, cant wait!", user.getAppUserId(), seller.getAppUserId(), product.getProductId(), timestamp);


        when(appUserRepository.findById(review.getSeller().getAppUserId())).thenReturn(Optional.of(seller));
        when(appUserRepository.findById(user.getAppUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(repository.save(any(Review.class))).thenReturn(review);
        when(orderRepository.findByUser(any(AppUser.class))).thenReturn(List.of(order));
        when(orderProductRepository.findByOrder(any(Order.class))).thenReturn(List.of(orderProduct));
        when(shipmentRepository.findByOrder(any(Order.class))).thenReturn(Optional.of(shipment));

        String reviewJson = jsonMapper.writeValueAsString(reviewCreateDTO);
        String expectedJson = jsonMapper.writeValueAsString(reviewResponseDTO);

        var request = post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void updateShouldReturn204() throws Exception {
        ReviewUpdateDTO reviewUpdateDTO = new ReviewUpdateDTO(1, 4, "Test Now");
        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO(1, 4, "Test Now", user.getAppUserId(), seller.getAppUserId(), product.getProductId(), timestamp);


        when(repository.findById(1)).thenReturn(Optional.of(review));
        when(appUserRepository.findById(review.getSeller().getAppUserId())).thenReturn(Optional.of(seller));
        when(appUserRepository.findById(user.getAppUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(repository.save(any(Review.class))).thenReturn(review);
        when(orderRepository.findByUser(any(AppUser.class))).thenReturn(List.of(order));
        when(orderProductRepository.findByOrder(any(Order.class))).thenReturn(List.of(orderProduct));
        when(shipmentRepository.findByOrder(any(Order.class))).thenReturn(Optional.of(shipment));

        String reviewJson = jsonMapper.writeValueAsString(reviewUpdateDTO);
        String expectedJson = jsonMapper.writeValueAsString(reviewResponseDTO);

        var request = put("/api/v1/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void updateShouldReturn409Conflict() throws Exception {
        ReviewUpdateDTO reviewUpdateDTO = new ReviewUpdateDTO(1, 4, "Test Now");
        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO(1, 4, "Test Now", user.getAppUserId(), seller.getAppUserId(), product.getProductId(), timestamp);


        String reviewJson = jsonMapper.writeValueAsString(review);

        var request = put("/api/v1/reviews/5")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    void deleteShouldReturn204NoContent() throws Exception {
        doNothing().when(repository).deleteById(1);

        var request = delete("/api/v1/reviews/1")
                .header("Authorization", "Bearer " + token);


        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
