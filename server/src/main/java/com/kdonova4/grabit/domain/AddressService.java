package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.domain.mapper.AddressMapper;
import com.kdonova4.grabit.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class AddressService {

    private final AddressRepository repository;
    private final AppUserRepository appUserRepository;

    public AddressService(AddressRepository repository, AppUserRepository appUserRepository) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
    }

    public List<Address> findAll() {
        return repository.findAll();
    }

    public List<Address> findByUser(AppUser user) {
        return repository.findAddressByUser(user);
    }

    public Optional<Address> findById(int id) {
        return repository.findById(id);
    }

    public Result<AddressResponseDTO> create(AddressCreateDTO address) {

        AppUser user = appUserRepository.findById(address.getUserId()).orElse(null);
        Address actualAddress = AddressMapper.toAddress(address, user);


        Result<AddressResponseDTO> result = validate(actualAddress);

        if(!result.isSuccess()) {
            return result;
        }

        if(actualAddress.getAddressId() != 0) {
            result.addMessages("AddressId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        actualAddress = repository.save(actualAddress);
        result.setPayload(AddressMapper.toResponseDTO(actualAddress));
        return result;
    }


    public Result<AddressResponseDTO> update(AddressUpdateDTO address) {

        AppUser user = appUserRepository.findById(address.getAddressId()).orElse(null);


        Address actualAddress = AddressMapper.toAddress(address, user);

        Result<AddressResponseDTO> result = validate(actualAddress);

        if(!result.isSuccess()) {
            return result;
        }

        if(address.getAddressId() <= 0) {
            result.addMessages("ADDRESS ID MUST BE SET", ResultType.INVALID);
            return result;
        }

        Optional<Address> oldAddress = repository.findById(address.getAddressId());
        if(oldAddress.isPresent()) {
            repository.save(actualAddress);
            return result;
        } else {
            result.addMessages("ADDRESS " + address.getAddressId() + " NOT FOUND", ResultType.NOT_FOUND);
            return result;
        }
    }

    private Result<AddressResponseDTO> validate(Address address) {
        Result<AddressResponseDTO> result = new Result<>();

        if(address == null) {
            result.addMessages("ADDRESS CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(address.getUser() == null) {
            result.addMessages("USER CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(address.getUser().getAppUserId() <= 0 || !appUserRepository.findById(address.getUser().getAppUserId()).isPresent()) {
            result.addMessages("USER MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(address.getStreet() == null || address.getStreet().isBlank()) {
            result.addMessages("STREET CANNOT BE NULL OR BLANK", ResultType.INVALID);
        }

        if(address.getCity() == null || address.getCity().isBlank()) {
            result.addMessages("CITY CANNOT BE NULL OR BLANK", ResultType.INVALID);
        }

        if(address.getState() == null || address.getState().isBlank()) {
            result.addMessages("STATE CANNOT BE NULL OR BLANK", ResultType.INVALID);
        }

        if(address.getZipCode() == null || address.getZipCode().isBlank()) {
            result.addMessages("ZIP CODE CANNOT BE NULL OR BLANK", ResultType.INVALID);
        }

        if(address.getCountry() == null || address.getCountry().isBlank()) {
            result.addMessages("COUNTRY CANNOT BE NULL OR BLANK", ResultType.INVALID);
        }

        return result;

    }
}
