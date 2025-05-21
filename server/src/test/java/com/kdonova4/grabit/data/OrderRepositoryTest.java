package com.kdonova4.grabit.data;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class OrderRepositoryTest {

    @Autowired
    OrderRepository repository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }
}
