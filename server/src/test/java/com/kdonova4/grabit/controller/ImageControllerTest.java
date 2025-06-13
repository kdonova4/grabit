package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ImageRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.DiscountType;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest {

    @MockBean
    ImageRepository repository;

    @MockBean
    AppUserRepository appUserRepository;

    @MockBean
    ProductRepository productRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtConverter jwtConverter;

    String token;

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private AppUser user;
    private AppRole role;
    private Product product;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        role = new AppRole(1, "SELLER", Set.of(user));
        user.setRoles(Set.of(role));
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, null);

        when(appUserRepository.findByUsername("kdonova4")).thenReturn(Optional.of(user));
        token = jwtConverter.getTokenFromUser(user);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        var request = get("/api/v1/images");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByProductShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/images/product/1");
        Image image = new Image(1, "http://example.com/laptop2.jpg", product);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(repository.findByProduct(any(Product.class))).thenReturn(List.of(image));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByProductShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/images/product/1");
        Image image = new Image(1, "http://example.com/laptop2.jpg", product);

        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(repository.findByProduct(any(Product.class))).thenReturn(List.of(image));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {
        Image image = new Image(1, "http://example.com/laptop2.jpg", product);

        when(repository.findById(1)).thenReturn(Optional.of(image));

        String imageJson = jsonMapper.writeValueAsString(image);

        var request = get("/api/v1/images/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(imageJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var request = get("/api/v1/images/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenInvalid() throws Exception {
        Image image = new Image();

        String imageJson = jsonMapper.writeValueAsString(image);

        var request = post("/api/v1/images")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(imageJson);

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
        Image image = new Image(0, "http://example.com/laptop2.jpg", product);
        Image expected = new Image(1, "http://example.com/laptop2.jpg", product);

        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(repository.save(any(Image.class))).thenReturn(expected);

        String imageJson = jsonMapper.writeValueAsString(image);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/v1/images")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(imageJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void deleteShouldReturn204NoContent() throws Exception {
        doNothing().when(repository).deleteById(1);

        var request = delete("/api/v1/images/1")
                .header("Authorization", "Bearer " + token);


        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
