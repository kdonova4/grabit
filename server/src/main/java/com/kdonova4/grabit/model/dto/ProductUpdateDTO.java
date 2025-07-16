package com.kdonova4.grabit.model.dto;

import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDTO {

    private int productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private ConditionType condition;
    private int quantity;
    private ProductStatus status;
    private BigDecimal winningBid;
    private int categoryId;
}
