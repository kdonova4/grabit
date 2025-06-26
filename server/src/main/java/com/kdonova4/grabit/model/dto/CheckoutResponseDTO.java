package com.kdonova4.grabit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponseDTO {
    private OrderResponseDTO orderResponseDTO;
    private ShipmentResponseDTO shipmentResponseDTO;
    private PaymentResponseDTO paymentResponseDTO;
}
