package com.kdonova4.grabit.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BidMessage {
    private int userId;
    private int productId;
    private BigDecimal bidAmount;
}
