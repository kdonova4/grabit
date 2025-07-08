package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.model.dto.*;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductAuctionResponseDTO toAuctionResponse(Product product) {
        List<Integer> categoryIds = product.getCategories()
                .stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());

        return new ProductAuctionResponseDTO(
                product.getProductId(),
                product.getPostedAt(),
                product.getSaleType(),
                product.getProductStatus(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getAuctionEnd(),
                product.getWinningBid(),
                product.getUser().getAppUserId(),
                categoryIds
        );
    }

    public static ProductBuyNowResponseDTO toBuyNowResponse(Product product) {
        List<Integer> categoryIds = product.getCategories()
                .stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());

        return new ProductBuyNowResponseDTO(
                product.getProductId(),
                product.getPostedAt(),
                product.getSaleType(),
                product.getProductStatus(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getOfferPrice(),
                product.getUser().getAppUserId(),
                categoryIds
        );
    }

    public static ProductResponseDTO toResponseDTO(Product product) {
        List<Integer> categoryIds = product.getCategories()
                .stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());

        return new ProductResponseDTO(
                product.getProductId(),
                product.getPostedAt(),
                product.getSaleType(),
                product.getProductStatus(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getAuctionEnd(),
                product.getWinningBid(),
                product.getOfferPrice(),
                product.getUser().getAppUserId(),
                categoryIds
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
                ProductStatus.ACTIVE,
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
                product.getOfferPrice(),
                product.getUser()
        );
    }

    public static ProductUpdateDTO toUpdateDTO(Product product) {
        List<Integer> categoryIds = product.getCategories()
                .stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());

        return new ProductUpdateDTO(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getProductStatus(),
                product.getWinningBid(),
                categoryIds
        );
    }
}
