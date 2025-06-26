package com.kdonova4.grabit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferCreateDTO {

    private BigDecimal offerAmount;
    private String message;
    private int userId;
    private int productId;
}
