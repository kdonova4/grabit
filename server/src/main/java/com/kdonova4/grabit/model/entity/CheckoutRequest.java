package com.kdonova4.grabit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {
    private Order order;
    private Shipment shipment;
    private Payment payment;
    private List<ShoppingCart> cartItems;
}
