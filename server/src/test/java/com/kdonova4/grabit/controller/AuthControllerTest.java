package com.kdonova4.grabit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.model.entity.AppRole;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.dto.RegisterRequest;
import com.kdonova4.grabit.security.AppUserService;
import com.kdonova4.grabit.security.JwtConverter;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @MockBean
    AppUserRepository repository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    AppUserService service;

    @Autowired
    MockMvc mockMvc;

    @MockBean
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

        when(repository.findByUsername("kdonova4")).thenReturn(Optional.of(user));
        token = jwtConverter.getTokenFromUser(user);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findByIdShouldReturn200WhenIdFound() throws Exception {

        when(service.findUserById(1)).thenReturn(Optional.of(user));

        String userJson = jsonMapper.writeValueAsString(user);

        var request = get("/api/v1/users/1");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(userJson));
    }

    @Test
    void findByIdShouldReturn404WhenIdNotFound() throws Exception {
        when(service.findUserById(1)).thenReturn(Optional.empty());


        var request = get("/api/v1/users/1");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void authenticateShouldReturn200() throws Exception {
        // Arrange
        Map<String, String> credentials = Map.of(
                "username", "kdonova4",
                "password", "85c*98Kd"
        );

        AppUser mockUser = new AppUser();
        mockUser.setUsername("kdonova4");
        mockUser.setAppUserId(1);
        mockUser.setRoles(Set.of(new AppRole(1, "BUYER", Set.of(mockUser)))); // 👈 important

        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(jwtConverter.getTokenFromUser(mockUser)).thenReturn("mock-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "kdonova4",
                          "password": "85c*98Kd"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt_token").value("mock-jwt-token"));
    }

    @Test
    void authenticateShouldReturn403WhenInvalidCredentials() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "username": "invalid_user",
                      "password": "wrong_password"
                    }
                    """))
                .andExpect(status().isForbidden());
    }

    @Test
    void createAccountShouldReturn201() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setRoles(List.of("BUYER"));

        AppUser mockUser = new AppUser();
        mockUser.setAppUserId(42);

        when(service.create(
                eq("newuser@example.com"),
                eq("newuser"),
                eq("password123"),
                eq(List.of("BUYER"))
        )).thenReturn(mockUser);

        String json = """
            {
              "username": "newuser",
              "email": "newuser@example.com",
              "password": "password123",
              "roles": ["BUYER"]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appUserId").value(42));
    }

    @Test
    void createAccountShouldReturn400WhenValidationException() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("baduser");
        request.setEmail("baduser@example.com");
        request.setPassword("badpass");
        request.setRoles(List.of("BUYER"));

        when(service.create(any(), any(), any(), any()))
                .thenThrow(new ValidationException("Invalid data"));

        String json = """
            {
              "username": "baduser",
              "email": "baduser@example.com",
              "password": "badpass",
              "roles": ["BUYER"]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Invalid data"));
    }

    @Test
    void createAccountShouldReturn400WhenDuplicateKeyException() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicateuser");
        request.setEmail("dup@example.com");
        request.setPassword("password");
        request.setRoles(List.of("BUYER"));

        when(service.create(any(), any(), any(), any()))
                .thenThrow(new DuplicateKeyException("Duplicate key"));

        String json = """
            {
              "username": "duplicateuser",
              "email": "dup@example.com",
              "password": "password",
              "roles": ["BUYER"]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("The provided username already exists"));
    }
}
