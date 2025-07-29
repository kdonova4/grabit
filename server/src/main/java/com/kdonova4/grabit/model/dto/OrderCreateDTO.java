package com.kdonova4.grabit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDTO {

    private int userId;
    private int shippingAddressId;
    private int billingAddressId;

}
