package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.*;

public class ProductCategoryMapper {

    public static ProductCategoryResponseDTO toResponseDTO(ProductCategory productCategory) {
        return new ProductCategoryResponseDTO(
                productCategory.getProductCategoryId(),
                productCategory.getProduct().getProductId(),
                productCategory.getCategory().getCategoryId()
        );
    }

    public static ProductCategory toProductCategory(Product product, Category category) {
        return new ProductCategory(
                0,
                product,
                category
        );
    }
}
