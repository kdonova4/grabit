package com.kdonova4.grabit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDTO {

    private int userId;
    private int shippingAddressId;
    private int billingAddressId;
    private BigDecimal totalAmount;
    private List<OrderProductDTO> orderProducts;

}
