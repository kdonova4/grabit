package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    AddressRepository addressRepository;

    @Mock
    AppUserRepository appUserRepository;

    @InjectMocks
    AddressService service;

    private Address address;
    private AppUser user;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        address = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);

    }

    @Test
    void shouldFindAll() {
        when(addressRepository.findAll()).thenReturn(
                List.of(
                        new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", null)

                )
        );

        List<Address> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(addressRepository).findAll();
    }

    @Test
    void shouldFindByUser() {

        when(addressRepository.findAddressByUser(user)).thenReturn(
                List.of(
                        address
                )
        );

        List<Address> actual = service.findByUser(user);

        assertEquals(1, actual.size());
        verify(addressRepository).findAddressByUser(user);
    }

    @Test
    void shouldFindById() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));

        Optional<Address> actual = service.findById(1);
        assertTrue(actual.isPresent());
    }

    @Test
    void shouldCreateValid() {
        Address mockOut = address;
        mockOut.setAddressId(1);
        address.setAddressId(0);

        when(addressRepository.save(address)).thenReturn(mockOut);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        Result<Address> actual = service.create(address);

        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {

        Result<Address> actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());

        address.setAddressId(0);
        address.setUser(null);
        actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());

        user.setAppUserId(0);
        actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());

        user.setAppUserId(1);
        address.setStreet(null);
        actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());

        address.setStreet("street");
        address.setCity(null);
        actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());

        address.setCity("Waxhaw");
        address.setState(null);
        actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());

        address.setState("North Carolina");
        address.setZipCode(null);
        actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());

        address.setZipCode("123123");
        address.setCountry(null);
        actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());

        address = null;
        actual = service.create(address);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldUpdate() {
        address.setStreet("New Street");

        when(addressRepository.save(address)).thenReturn(address);
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        Result<Address> actual = service.update(address);
        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotUpdateMissing() {
        address.setStreet("New Street");


        when(addressRepository.findById(address.getAddressId())).thenReturn(Optional.empty());
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        Result<Address> actual = service.update(address);
        assertEquals(ResultType.NOT_FOUND, actual.getType());
    }
}
