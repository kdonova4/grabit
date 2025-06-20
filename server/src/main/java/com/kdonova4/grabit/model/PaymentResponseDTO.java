package com.kdonova4.grabit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private int paymentId;
    private int orderId;
    private BigDecimal amountPaid;
    private Timestamp paidAt;
}
