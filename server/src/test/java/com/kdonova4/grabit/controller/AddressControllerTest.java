package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.enums.DiscountType;
import com.kdonova4.grabit.model.*;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    @MockBean
    AddressRepository repository;

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
        var request = get("/api/v1/addresses");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByUserShouldReturn200IfFound() throws Exception {
        var request = get("/api/v1/addresses/user/1");
        Address address = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(repository.findAddressByUser(any(AppUser.class))).thenReturn(List.of(address));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByUserShouldReturn404IfNotFound() throws Exception {
        var request = get("/api/v1/addresses/user/1");

        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {
        Address address = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);

        when(repository.findById(1)).thenReturn(Optional.of(address));

        String addressJson = jsonMapper.writeValueAsString(address);

        var request = get("/api/v1/addresses/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(addressJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var request = get("/api/v1/addresses/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenEmpty() throws Exception {
        var request = post("/api/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn400WhenInvalid() throws Exception {
        Address address = new Address();

        String addressJson = jsonMapper.writeValueAsString(address);

        var request = post("/api/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(addressJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn415WhenMultipart() throws Exception {
        Address address = new Address();

        String addressJson = jsonMapper.writeValueAsString(address);

        var request = post("/api/v1/addresses")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(addressJson);

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createShouldReturn201() throws Exception {
        Address address = new Address(0, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);
        Address expected = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);


        when(repository.save(any(Address.class))).thenReturn(expected);
        when(appUserRepository.findById(user.getAppUserId())).thenReturn(Optional.of(user));

        String addressJson = jsonMapper.writeValueAsString(address);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(addressJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void updateShouldReturn204() throws Exception {
        Address address = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);
        Address expected = new Address(1, "3504 Rune Rd", "Waxhaw", "NC", "28173", "USA", user);


        when(repository.save(any(Address.class))).thenReturn(expected);
        when(repository.findById(1)).thenReturn(Optional.of(address));
        when(appUserRepository.findById(user.getAppUserId())).thenReturn(Optional.of(user));

        String addressJson = jsonMapper.writeValueAsString(address);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = put("/api/v1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(addressJson);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
