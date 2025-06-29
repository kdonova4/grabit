package com.kdonova4.grabit.data;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class ProductCategoryRepositoryTest {

    @Autowired
    ProductCategoryRepository repository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByProduct() {
        Optional<Product> product = productRepository.findById(1);

        List<ProductCategory> productCategories = repository.findByProduct(product.get());

        assertEquals(1, productCategories.size());
    }

    @Test
    void shouldFindByCategory() {
        Optional<Category> category = categoryRepository.findById(1);
        List<ProductCategory> productCategories = repository.findByCategory(category.get());

        assertEquals(2, productCategories.size());
    }

    @Test
    void shouldFindByProductAndCategory() {
        Optional<Product> product = productRepository.findById(1);
        Optional<Category> category = categoryRepository.findById(1);

        Optional<ProductCategory> productCategory = repository.findByCategoryAndProduct(category.get(), product.get());

        assertNotNull(productCategory.get());
    }

    @Test
    void shouldCreate() {
        Optional<Product> product = productRepository.findById(2);
        Optional<Category> category = categoryRepository.findById(1);

        ProductCategory productCategory = new ProductCategory(0, product.get(), category.get());
        repository.save(productCategory);

        assertEquals(4, repository.findAll().size());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);

        assertEquals(2, repository.findAll().size());
    }
}
