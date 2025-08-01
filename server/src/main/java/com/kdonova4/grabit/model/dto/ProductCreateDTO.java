package com.kdonova4.grabit.model.dto;

import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDTO {
    private SaleType saleType;
    private String productName;
    private String description;
    private BigDecimal price;
    private ConditionType conditionType;
    private int quantity;
    private int userId;
    private int categoryId;
}
