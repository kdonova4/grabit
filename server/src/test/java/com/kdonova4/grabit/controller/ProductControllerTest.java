package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.enums.ShipmentStatus;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @MockBean
    ProductRepository repository;

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
        var request = get("/api/v1/products");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByUserShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/products/user/1");
        Product product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(repository.findByUser(any(AppUser.class))).thenReturn(List.of(product));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByUserShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/products/user/1");

        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {
        Product product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);

        when(repository.findById(1)).thenReturn(Optional.of(product));

        String productJson = jsonMapper.writeValueAsString(product);

        var request = get("/api/v1/products/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(productJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());


        var request = get("/api/v1/products/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void searchShouldReturn200() throws Exception {
        Product product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);

        String productName = product.getProductName();

        var request = get("/api/v1/products/search").param("productName", productName);

        when(repository.search(productName, null, null, null, null, null, null)).thenReturn(List.of(product));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void createShouldReturn400WhenEmpty() throws Exception {
        var request = post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn400WhenInvalid() throws Exception {
        Product product = new Product();

        String productJson = jsonMapper.writeValueAsString(product);

        var request = post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(productJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn415WhenMultipart() throws Exception {
        Product product = new Product();

        String productJson = jsonMapper.writeValueAsString(product);

        var request = post("/api/v1/products")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(productJson);

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createShouldReturn201() throws Exception {
        Product product = new Product(0, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);
        Product expected = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);


        when(repository.save(any(Product.class))).thenReturn(expected);
        when(appUserRepository.findById(user.getAppUserId())).thenReturn(Optional.of(user));

        String productJson = jsonMapper.writeValueAsString(product);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(productJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void updateShouldReturn204() throws Exception {
        Product product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);
        Product expected = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1400), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);


        when(repository.save(any(Product.class))).thenReturn(expected);
        when(repository.findById(1)).thenReturn(Optional.of(product));
        when(appUserRepository.findById(user.getAppUserId())).thenReturn(Optional.of(user));

        String productJson = jsonMapper.writeValueAsString(product);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(productJson);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturn204NoContent() throws Exception {
        doNothing().when(repository).deleteById(1);

        var request = delete("/api/v1/products/1")
                .header("Authorization", "Bearer " + token);


        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
