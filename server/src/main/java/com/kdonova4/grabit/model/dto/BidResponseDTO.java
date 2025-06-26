package com.kdonova4.grabit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidResponseDTO {
    private int bidId;
    private BigDecimal bidAmount;
    private Timestamp placedAt;
    private int productId;
    private int userId;
}
