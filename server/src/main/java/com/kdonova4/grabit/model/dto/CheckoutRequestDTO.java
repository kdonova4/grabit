package com.kdonova4.grabit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequestDTO {

    private OrderCreateDTO orderDTO;
    private List<ShoppingCartDTO> cartList;
}
