package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    List<ProductCategory> findByProduct(Product product);

    List<ProductCategory> findByCategory(Category category);

    Optional<ProductCategory> findByCategoryAndProduct(Category category, Product product);
}
