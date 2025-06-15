package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.CategoryRepository;
import com.kdonova4.grabit.domain.CategoryService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.ResultType;
import com.kdonova4.grabit.model.AppRole;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Category;
import com.kdonova4.grabit.security.JwtConverter;
import com.kdonova4.grabit.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.postgresql.hostchooser.HostRequirement.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @MockBean
    CategoryRepository repository;

    @MockBean
    CategoryService service;

    @MockBean
    AppUserRepository appUserRepository;

    @MockBean
    JwtConverter jwtConverter;  // <-- mock JwtConverter to avoid context issues

    @Autowired
    MockMvc mockMvc;

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

        // mock token return value
        when(jwtConverter.getTokenFromUser(any())).thenReturn("fake-jwt-token");
        token = jwtConverter.getTokenFromUser(user);

        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        var request = get("/api/v1/categories");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.empty());

        var request = get("/api/v1/categories/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenEmpty() throws Exception {
        var request = post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn400WhenInvalid() throws Exception {
        Category category = new Category();

        String categoryJson = jsonMapper.writeValueAsString(category);

        Result<Category> result = new Result<>();
        result.addMessages("Invalid", ResultType.INVALID);

        when(service.create(any(Category.class))).thenReturn(result);

        var request = post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(categoryJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn415WhenMultipart() throws Exception {
        Category category = new Category();

        String categoryJson = jsonMapper.writeValueAsString(category);

        var request = post("/api/v1/categories")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(categoryJson);

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createShouldReturn201() throws Exception {
        Category category = new Category(0, "Test");
        Category expected = new Category(1, "Test");

        Result<Category> result = new Result<>();
        result.setPayload(expected);

        when(service.create(any(Category.class))).thenReturn(result);

        String categoryJson = jsonMapper.writeValueAsString(category);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(categoryJson);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void deleteShouldReturn204NoContent() throws Exception {
        doNothing().when(repository).deleteById(1);

        var request = delete("/api/v1/categories/1")
                .header("Authorization", "Bearer " + token);


        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
