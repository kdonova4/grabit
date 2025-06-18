package com.kdonova4.grabit.model;

import lombok.Data;

@Data
public class OrderPlacedEvent {
    private final int orderId;

    public OrderPlacedEvent(int orderId) {
        this.orderId = orderId;
    }


}
