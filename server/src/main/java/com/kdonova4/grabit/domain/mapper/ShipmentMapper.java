package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.Shipment;
import com.kdonova4.grabit.model.ShipmentResponseDTO;

public class ShipmentMapper {

    public static ShipmentResponseDTO toResponse(Shipment shipment) {
        return new ShipmentResponseDTO(
                shipment.getShipmentId(),
                shipment.getOrder().getOrderId(),
                shipment.getShipmentStatus(),
                shipment.getTrackingNumber(),
                shipment.getShippedAt(),
                shipment.getDeliveredAt());
    }
}
