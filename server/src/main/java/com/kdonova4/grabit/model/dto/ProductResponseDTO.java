package com.kdonova4.grabit.model.dto;

import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private int productId;
    private Timestamp postedAt;
    private SaleType saleType;
    private ProductStatus productStatus;
    private String productName;
    private String description;
    private BigDecimal price;
    private ConditionType conditionType;
    private int quantity;
    private LocalDateTime auctionEnd;
    private BigDecimal winningBid;
    private int userId;

}
