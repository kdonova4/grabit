package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppRoleRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppRole;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.security.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceTest {

    @Mock
    AppUserRepository appUserRepository;

    @Mock
    AppRoleRepository appRoleRepository;

    @Mock
    AddressRepository addressRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    EmailService emailService;

    @InjectMocks
    AppUserService service;

    private AppUser user;
    private AppRole role;
    private Address address;

    @BeforeEach
    void setup() {

        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "$2y$10$5XmabI6UghCVaIDJrvgHxeWe.vhe6Htd.QANZJ4RIkPzPHtpirP0y", true, new HashSet<>());
        role = new AppRole(1, "SELLER", Set.of(user));
        user.setRoles(Set.of(role));
        address = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);
    }

    @Test
    void findUserById() {
        when(appUserRepository.findById(user.getAppUserId())).thenReturn(Optional.of(user));

        Optional<AppUser> appUser = service.findUserById(user.getAppUserId());

        assertTrue(appUser.isPresent());
        verify(appUserRepository).findById(user.getAppUserId());
    }

    @Test
    void shouldCreateValid() {
        // Arrange
        String username = "kdonova4";
        String password = "85c*98Kd";
        String email = "kdonova4@gmail.com";
        String encodedPassword = "$2y$10$5XmabI6UghCVaIDJrvgHxeWe.vhe6Htd.QANZJ4RIkPzPHtpirP0y";  // Mocked encoded password

        // Mocking the PasswordEncoder to return the encoded password
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // Creating the expected AppUser object with the encoded password
        AppUser appUser = new AppUser(0, username, email, encodedPassword, true, Set.of(role));

        // Mocking the repository create method
        when(appUserRepository.save(appUser)).thenReturn(user);
        when(appRoleRepository.findByRoleName("SELLER")).thenReturn(Optional.of(role));
        // Act
        AppUser actual = service.create(email, username, password, List.of("SELLER"));

        // Assert
        assertNotNull(actual);
        assertEquals(username, actual.getUsername());
        assertEquals(encodedPassword, actual.getPassword());  // Check the encoded password
        assertTrue(actual.isDisabled());  // Check the default value for enabled
    }

}
