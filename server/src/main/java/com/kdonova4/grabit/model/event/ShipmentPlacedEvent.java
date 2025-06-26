package com.kdonova4.grabit.model.event;

import lombok.Data;

@Data
public class ShipmentPlacedEvent {
    private final int shipmentId;

    public ShipmentPlacedEvent(int shipmentId) {
        this.shipmentId = shipmentId;
    }
}
