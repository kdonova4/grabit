package com.kdonova4.grabit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BidCreateDTO {
    private int userId;
    private int productId;
    private BigDecimal bidAmount;
}
