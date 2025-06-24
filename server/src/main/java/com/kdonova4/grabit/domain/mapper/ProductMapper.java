package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.*;

public class ProductMapper {

    public static ProductAuctionResponseDTO toAuctionResponse(Product product) {
        return new ProductAuctionResponseDTO(
                product.getProductId(),
                product.getPostedAt(),
                product.getSaleType(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getAuctionEnd(),
                product.getWinningBid(),
                product.getUser().getAppUserId()
        );
    }

    public static ProductBuyNowResponseDTO toBuyNowResponse(Product product) {
        return new ProductBuyNowResponseDTO(
                product.getProductId(),
                product.getPostedAt(),
                product.getSaleType(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getUser().getAppUserId()
        );
    }

    public static ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getPostedAt(),
                product.getSaleType(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getAuctionEnd(),
                product.getWinningBid(),
                product.getUser().getAppUserId()
        );
    }

    public static Product toProduct(ProductCreateDTO productCreateDTO, AppUser user) {
        return new Product(
                0,
                null,
                productCreateDTO.getSaleType(),
                productCreateDTO.getProductName(),
                productCreateDTO.getDescription(),
                productCreateDTO.getPrice(),
                productCreateDTO.getConditionType(),
                productCreateDTO.getQuantity(),
                null,
                null,
                null,
                user
        );
    }

    public static Product toProduct(ProductUpdateDTO productUpdateDTO, Product product) {
        return new Product(
                productUpdateDTO.getProductId(),
                product.getPostedAt(),
                product.getSaleType(),
                productUpdateDTO.getProductName(),
                productUpdateDTO.getDescription(),
                productUpdateDTO.getPrice(),
                productUpdateDTO.getCondition(),
                productUpdateDTO.getQuantity(),
                product.getProductStatus(),
                product.getAuctionEnd(),
                product.getWinningBid(),
                product.getUser()
        );
    }

    public static ProductUpdateDTO toUpdateDTO(Product product) {
        return new ProductUpdateDTO(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getProductStatus(),
                product.getWinningBid()
                );
    }
}
