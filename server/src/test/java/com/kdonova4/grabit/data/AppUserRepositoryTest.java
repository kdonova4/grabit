package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppRole;
import com.kdonova4.grabit.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class AppUserRepositoryTest {

    @Autowired
    AppUserRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByUsername() {
        Optional<AppUser> user = repository.findByUsername("dono2223");

        assertTrue(user.isPresent());
    }

    @Test
    void shouldFineByUsernameAndPassword() {
        Optional<AppUser> user = repository.findByUsernameAndPassword("dono2223", "$2y$10$5XmabI6UghCVaIDJrvgHxeWe.vhe6Htd.QANZJ4RIkPzPHtpirP0y");

        assertTrue(user.isPresent());
    }

    @Test
    void shouldCreate() {
        AppUser user = new AppUser(0, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());

        assertNotNull(repository.findByUsername("kdonova4"));
    }

    @Test
    void shouldUpdate() {
        Optional<AppUser> user = repository.findById(3);
        user.get().setEmail("kdonova2223@gmail.com");
        repository.save(user.get());

        assertEquals("kdonova2223@gmail.com", repository.findById(3).get().getEmail());
    }

}
