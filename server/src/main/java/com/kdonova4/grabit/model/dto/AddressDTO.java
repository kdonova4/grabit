package com.kdonova4.grabit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private int addressId;

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    private int userId;
}
