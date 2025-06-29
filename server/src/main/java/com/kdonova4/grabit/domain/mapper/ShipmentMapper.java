package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.Shipment;
import com.kdonova4.grabit.model.dto.ShipmentCreateDTO;
import com.kdonova4.grabit.model.dto.ShipmentResponseDTO;

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

    public static Shipment toShipment(ShipmentCreateDTO shipmentCreateDTO, Order order, String trackingNumber) {
        return new Shipment(
                0,
                order,
                null,
                trackingNumber,
                null,
                null
        );
    }

    public static Shipment toShipment(ShipmentResponseDTO shipmentResponseDTO, Order order) {
        return new Shipment(
                shipmentResponseDTO.getShipmentId(),
                order,
                shipmentResponseDTO.getShipmentStatus(),
                shipmentResponseDTO.getTrackingNumber(),
                shipmentResponseDTO.getShippedAt(),
                shipmentResponseDTO.getDeliveredAt()
        );
    }
}
