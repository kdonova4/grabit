package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.dto.RegisterRequest;
import com.kdonova4.grabit.security.AppUserService;
import com.kdonova4.grabit.security.JwtConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AppUser Controller", description = "AppUser Operations")
@RequestMapping("/api/v1/users")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtConverter jwtConverter;
    private final AppUserService service;

    public AuthController(AuthenticationManager authenticationManager, JwtConverter jwtConverter, AppUserService appUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtConverter = jwtConverter;
        this.service = appUserService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Finds User By ID")
    public ResponseEntity<AppUser> findById(@PathVariable int userId) {
        Optional<AppUser> appUser = service.findUserById(userId);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(appUser.get());
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticates a User")
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody Map<String, String> credentials) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(credentials.get("username"), credentials.get("password"));

        try {
            Authentication authentication = authenticationManager.authenticate(authToken);

            if (authentication.isAuthenticated()) {
                String jwtToken = jwtConverter.getTokenFromUser((AppUser) authentication.getPrincipal());

                HashMap<String, String> map = new HashMap<>();
                map.put("jwt_token", jwtToken);

                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (AuthenticationException ex) {
            System.out.println(ex);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> createAccount(@RequestBody RegisterRequest request) {
        AppUser appUser = null;

        try {
            String username = request.getUsername();
            String email = request.getEmail();
            String password = request.getPassword();
            List<String> roles = request.getRoles();

            appUser = service.create(email, username, password, roles);
        } catch (ValidationException ex) {
            return new ResponseEntity<>(List.of(ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(List.of("The provided username already exists"), HttpStatus.BAD_REQUEST);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("appUserId", appUser.getAppUserId());
        map.put("email", appUser.getEmail());

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        int code = Integer.parseInt(credentials.get("code"));

        if(service.validateCode(email, code)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code");
    }
}
