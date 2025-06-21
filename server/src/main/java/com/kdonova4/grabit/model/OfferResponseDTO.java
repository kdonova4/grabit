package com.kdonova4.grabit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferResponseDTO {

    private int offerId;
    private BigDecimal offerAmount;
    private Timestamp sentAt;
    private String message;
    private int userId;
    private int productId;
    private LocalDateTime expireDate;
}
