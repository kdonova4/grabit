package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.Address;
import com.kdonova4.grabit.model.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class AddressRepositoryTest {

    @Autowired
    AddressRepository repository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByUser() {
        Optional<AppUser> appUser = appUserRepository.findById(1);
        List<Address> addresses = repository.findAddressByUser(appUser.get());

        assertEquals(1, addresses.size());
    }

    @Test
    void shouldCreate() {
        Optional<AppUser> appUser = appUserRepository.findById(1);
        Address address = new Address(0, "345 Apple St", "Waxhaw", "NC", "28173", "USA", appUser.get());
        repository.save(address);

        List<Address> addresses = repository.findAddressByUser(appUser.get());

        assertEquals(2, addresses.size());
    }

    @Test
    void shouldUpdate() {
        Optional<AppUser> appUser = appUserRepository.findById(1);
        Address address = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", appUser.get());
        repository.save(address);
        assertEquals("345 Apple St", repository.findAddressByUser(appUser.get()).get(0).getStreet());
    }

}
