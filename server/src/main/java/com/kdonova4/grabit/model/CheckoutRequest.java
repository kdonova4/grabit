package com.kdonova4.grabit.model;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    private Order order;
    private Shipment shipment;
    private Payment payment;
    private List<ShoppingCart> cartItems;

}
