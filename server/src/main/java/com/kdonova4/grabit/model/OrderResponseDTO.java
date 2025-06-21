package com.kdonova4.grabit.model;

import com.kdonova4.grabit.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private int orderId;
    private int userId;
    private Timestamp orderedAt;
    private int shippingAddressId;
    private int billingAddressId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderProductResponseDTO> orderProducts;
}
