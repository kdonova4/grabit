package com.kdonova4.grabit.model;

import lombok.Data;

@Data
public class ShipmentPlacedEvent {
    private final int shipmentId;

    public ShipmentPlacedEvent(int shipmentId) {
        this.shipmentId = shipmentId;
    }
}
