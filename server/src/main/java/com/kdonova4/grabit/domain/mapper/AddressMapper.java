package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.dto.AddressCreateDTO;
import com.kdonova4.grabit.model.dto.AddressResponseDTO;
import com.kdonova4.grabit.model.dto.AddressUpdateDTO;
import com.kdonova4.grabit.model.entity.*;

public class AddressMapper {

    public static AddressResponseDTO toResponseDTO(Address address) {
        return new AddressResponseDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getUser().getAppUserId()
        );
    }

    public static Address toAddress(AddressUpdateDTO addressUpdateDTO, AppUser user) {
        return new Address(
                addressUpdateDTO.getAddressId(),
                addressUpdateDTO.getStreet(),
                addressUpdateDTO.getCity(),
                addressUpdateDTO.getState(),
                addressUpdateDTO.getZipCode(),
                addressUpdateDTO.getCountry(),
                user
        );
    }

    public static Address toAddress(AddressCreateDTO addressCreateDTO, AppUser user) {
        return new Address(
                0,
                addressCreateDTO.getStreet(),
                addressCreateDTO.getCity(),
                addressCreateDTO.getState(),
                addressCreateDTO.getZipCode(),
                addressCreateDTO.getCountry(),
                user
        );
    }
}
