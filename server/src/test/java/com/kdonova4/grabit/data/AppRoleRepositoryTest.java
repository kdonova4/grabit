package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class AppRoleRepositoryTest {

    @Autowired
    AppRoleRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByRoleName() {
        Optional<AppRole> role = repository.findByRoleName("SELLER");

        assertNotNull(role.get());
    }
}
