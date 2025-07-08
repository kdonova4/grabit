package com.kdonova4.grabit.data;

import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByUser(AppUser user);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN ProductCategory pc ON pc.product = p " +
            "WHERE (:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice) AND "
            + "(:status IS NULL OR p.productStatus = :status) AND "
            + "(:condition IS NULL OR p.condition = :condition) AND "
            + "(:saleType IS NULL OR p.saleType = :saleType) AND "
            + "(:category IS NULL OR pc.category = :category)")
    List<Product> search(
            @Param("productName") String productName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") ProductStatus status,
            @Param("condition") ConditionType condition,
            @Param("saleType") SaleType saleType,
            @Param("category") Category category
    );

    List<Product> findBySaleTypeAndProductStatusAndAuctionEndBefore(SaleType saleType, ProductStatus status, LocalDateTime now);

    @Query("SELECT pc.product FROM ProductCategory pc WHERE pc.category.id = :categoryId")
    List<Product> findAllByCategoryId(@Param("categoryId") Integer categoryId);
}
