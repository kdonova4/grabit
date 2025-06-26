package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.CategoryRepository;
import com.kdonova4.grabit.data.ProductCategoryRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.dto.ProductCategoryCreateDTO;
import com.kdonova4.grabit.model.dto.ProductCategoryResponseDTO;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class ProductCategoryServiceTest {

    @Mock
    ProductCategoryRepository productCategoryRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    ProductCategoryService service;

    private Product product;
    private Category category;
    private ProductCategory productCategory;

    @BeforeEach
    void setup() {
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, null,null);
        category = new Category(1, "Electronics");
        productCategory = new ProductCategory(1, product, category);
    }

    @Test
    void shouldFindAll() {
        when(productCategoryRepository.findAll()).thenReturn(
                List.of(
                        productCategory
                )
        );

        List<ProductCategory> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(productCategoryRepository).findAll();
    }

    @Test
    void shouldFindByProduct() {
        when(productCategoryRepository.findByProduct(product)).thenReturn(
                List.of(
                        productCategory
                )
        );

        List<ProductCategory> actual = service.findByProduct(product);

        assertEquals(1, actual.size());
        verify(productCategoryRepository).findByProduct(product);
    }

    @Test
    void shouldFindByCategory() {
        when(productCategoryRepository.findByCategory(category)).thenReturn(
                List.of(
                        productCategory
                )
        );

        List<ProductCategory> actual = service.findByCategory(category);

        assertEquals(1, actual.size());
        verify(productCategoryRepository).findByCategory(category);
    }

    @Test
    void shouldFindByCategoryAndProduct() {
        when(productCategoryRepository.findByCategoryAndProduct(category, product)).thenReturn(Optional.of(productCategory));

        Optional<ProductCategory> actual = service.findByCategoryAndProduct(category, product);

        assertTrue(actual.isPresent());
    }

    @Test
    void shouldFindById() {
        when(productCategoryRepository.findById(productCategory.getProductCategoryId())).thenReturn(Optional.of(productCategory));
        Optional<ProductCategory> actual = service.findById(productCategory.getProductCategoryId());
        assertTrue(actual.isPresent());
    }

    @Test
    void shouldCreateValid() {
        ProductCategoryCreateDTO productCategoryCreateDTO = new ProductCategoryCreateDTO(product.getProductId(), category.getCategoryId());
        ProductCategory mockOut = new ProductCategory(1, product, category);

        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(mockOut);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        Result<ProductCategoryResponseDTO> actual = service.create(productCategoryCreateDTO);

        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotCreateInvalid() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        ProductCategoryCreateDTO productCategoryCreateDTO = new ProductCategoryCreateDTO(product.getProductId(), category.getCategoryId());

        Result<ProductCategoryResponseDTO> actual = service.create(productCategoryCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldDeleteById() {
        when(productCategoryRepository.findById(productCategory.getProductCategoryId())).thenReturn(Optional.of(productCategory));
        doNothing().when(productCategoryRepository).deleteById(productCategory.getProductCategoryId());
        assertTrue(service.deleteById(productCategory.getProductCategoryId()));
        verify(productCategoryRepository).deleteById(productCategory.getProductCategoryId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(productCategoryRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(productCategoryRepository, never()).deleteById(anyInt());
    }
}
