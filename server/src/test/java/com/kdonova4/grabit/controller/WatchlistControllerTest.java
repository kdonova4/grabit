package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.data.WatchlistRepository;
import com.kdonova4.grabit.enums.ConditionType;
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
public class WatchlistControllerTest {

    @MockBean
    WatchlistRepository repository;

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
    private Watchlist watchlist;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        role = new AppRole(1, "SELLER", Set.of(user));
        user.setRoles(Set.of(role));

        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);
        watchlist = new Watchlist(1 , product, user);


        when(appUserRepository.findByUsername("kdonova4")).thenReturn(Optional.of(user));
        token = jwtConverter.getTokenFromUser(user);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        var request = get("/api/v1/watchlists");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByUserShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/watchlists/user/1");
        Watchlist item = new Watchlist(1 , product, user);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(repository.findByUser(any(AppUser.class))).thenReturn(List.of(item));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByUserShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/watchlists/user/1");

        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByProductShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/watchlists/user/1/product/1");
        Watchlist item = new Watchlist(1 , product, user);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(repository.findByUserAndProduct(any(AppUser.class), any(Product.class))).thenReturn(Optional.of(item));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByProductShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/watchlists/user/1/product/1");

        when(appUserRepository.findById(1)).thenReturn(Optional.empty());
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {
        WatchlistDTO watchlistDTO = new WatchlistDTO(1 , product.getProductId(), user.getAppUserId());

        when(repository.findById(1)).thenReturn(Optional.of(watchlist));

        String watchJson = jsonMapper.writeValueAsString(watchlistDTO);

        var request = get("/api/v1/watchlists/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(watchJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var request = get("/api/v1/watchlists/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenInvalid() throws Exception {
        Watchlist watchlist = new Watchlist();

        String watchJson = jsonMapper.writeValueAsString(watchlist);

        var request = post("/api/v1/watchlists")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(watchJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn415WhenMultipart() throws Exception {
        Watchlist watchlist = new Watchlist();

        String watchJson = jsonMapper.writeValueAsString(watchlist);

        var request = post("/api/v1/watchlists")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(watchJson);

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createShouldReturn201() throws Exception {
        WatchlistDTO watchlistDTO = new WatchlistDTO(0 , product.getProductId(), user.getAppUserId());
        WatchlistDTO expected = new WatchlistDTO(1 , product.getProductId(), user.getAppUserId());

        when(appUserRepository.findById(user.getAppUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(repository.save(any(Watchlist.class))).thenReturn(watchlist);

        String shoppingJson = jsonMapper.writeValueAsString(watchlistDTO);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/v1/watchlists")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(shoppingJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void deleteShouldReturn204NoContent() throws Exception {
        doNothing().when(repository).deleteById(1);

        var request = delete("/api/v1/watchlists/1")
                .header("Authorization", "Bearer " + token);


        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
