package com.kdonova4.grabit.model;

import com.kdonova4.grabit.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentResponseDTO {
    private int shipmentId;
    private int orderId;
    private ShipmentStatus shipmentStatus;
    private String trackingNumber;
    private Timestamp shippedAt;
    private Timestamp deliveredAt;
}
