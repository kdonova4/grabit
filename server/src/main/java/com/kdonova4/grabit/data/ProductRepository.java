package com.kdonova4.grabit.data;

import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByUser(AppUser user);

    List<Product> findBySaleType(SaleType saleType);

    List<Product> findByCondition(ConditionType condition);

    List<Product> findByProductStatus(ProductStatus productStatus);

    List<Product> findByPriceGreaterThan(BigDecimal price);
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findBProductName(String productName);
}
