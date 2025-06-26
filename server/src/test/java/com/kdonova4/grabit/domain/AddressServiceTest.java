package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.model.dto.AddressCreateDTO;
import com.kdonova4.grabit.model.dto.AddressResponseDTO;
import com.kdonova4.grabit.model.dto.AddressUpdateDTO;
import com.kdonova4.grabit.model.entity.*;
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
                    address
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

        AddressCreateDTO addressCreateDTO = new AddressCreateDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getUser().getAppUserId()
        );

        when(addressRepository.save(address)).thenReturn(mockOut);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        Result<AddressResponseDTO> actual = service.create(addressCreateDTO);

        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotCreateInvalid() {

        AddressCreateDTO addressCreateDTO = new AddressCreateDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getUser().getAppUserId()
        );




        addressCreateDTO.setCity(null);
        Result<AddressResponseDTO> actual = service.create(addressCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        addressCreateDTO.setCity("Test");
        addressCreateDTO.setStreet(null);
        actual = service.create(addressCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        addressCreateDTO.setStreet("Test");
        addressCreateDTO.setState(null);
        actual = service.create(addressCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        addressCreateDTO.setState("Test");
        addressCreateDTO.setZipCode(null);
        actual = service.create(addressCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        addressCreateDTO.setZipCode("TEST");
        addressCreateDTO.setCountry(null);
        actual = service.create(addressCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        addressCreateDTO.setCountry("North Carolina");
        addressCreateDTO.setUserId(0);
        when(appUserRepository.findById(0)).thenReturn(Optional.empty());
        actual = service.create(addressCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

    }

    @Test
    void shouldUpdate() {
        address.setStreet("New Street");

        when(addressRepository.save(address)).thenReturn(address);
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getUser().getAppUserId()
        );
        Result<AddressResponseDTO> actual = service.update(addressUpdateDTO);
        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotUpdateMissing() {
        address.setStreet("New Street");


        when(addressRepository.findById(address.getAddressId())).thenReturn(Optional.empty());
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO(
                address.getAddressId(),
                "New",
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getUser().getAppUserId()
        );

        Result<AddressResponseDTO> actual = service.update(addressUpdateDTO);
        assertEquals(ResultType.NOT_FOUND, actual.getType());
    }
}
