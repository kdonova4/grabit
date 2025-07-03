package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.CategoryService;
import com.kdonova4.grabit.domain.ProductCategoryService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.mapper.ProductCategoryMapper;
import com.kdonova4.grabit.model.dto.ProductCategoryCreateDTO;
import com.kdonova4.grabit.model.dto.ProductCategoryResponseDTO;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ProductCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ProductCategory Controller", description = "ProductCategory Operations")
@RequestMapping("/api/v1/product-categories")
public class ProductCategoryController {

    private final ProductCategoryService service;
    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductCategoryController(ProductCategoryService service, ProductService productService, CategoryService categoryService) {
        this.service = service;
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Finds All Product Categories")
    public ResponseEntity<List<ProductCategoryResponseDTO>> findAll() {
        List<ProductCategory> productCategories = service.findAll();

        return ResponseEntity.ok(productCategories.stream().map(ProductCategoryMapper::toResponseDTO).toList());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Finds ProductCategories By Category")
    public ResponseEntity<List<ProductCategoryResponseDTO>> findByCategory(@PathVariable int categoryId) {
        Optional<Category> category = categoryService.findById(categoryId);

        if(category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ProductCategory> productCategories = service.findByCategory(category.get());

        return ResponseEntity.ok(productCategories.stream().map(ProductCategoryMapper::toResponseDTO).toList());
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Finds ProductCategories By Product")
    public ResponseEntity<List<ProductCategoryResponseDTO>> findByProduct(@PathVariable int productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ProductCategory> productCategories = service.findByProduct(product.get());

        return ResponseEntity.ok(productCategories.stream().map(ProductCategoryMapper::toResponseDTO).toList());
    }

    @GetMapping("/category/{categoryId}/product/{productId}")
    @Operation(summary = "Finds ProductCategories by Category and Product")
    public ResponseEntity<ProductCategoryResponseDTO> findByCategoryAndProduct(@PathVariable int categoryId, @PathVariable int productId) {
        Optional<Category> category = categoryService.findById(categoryId);
        Optional<Product> product = productService.findById(productId);

        if(category.isEmpty() || product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<ProductCategory> productCategory = service.findByCategoryAndProduct(category.get(), product.get());

        if(productCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ProductCategoryMapper.toResponseDTO(productCategory.get()));
    }

    @GetMapping("/{productCategoryId}")
    @Operation(summary = "Finds A ProductCategoryId By ID")
    public ResponseEntity<ProductCategoryResponseDTO> findById(@PathVariable int productCategoryId) {
        Optional<ProductCategory> productCategory = service.findById(productCategoryId);

        if(productCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ProductCategoryMapper.toResponseDTO(productCategory.get()));
    }

    @PostMapping
    @Operation(summary = "Creates A ProductCategory")
    public ResponseEntity<Object> create(@RequestBody ProductCategoryCreateDTO productCategoryCreateDTO) {
        Result<ProductCategoryResponseDTO> result = service.create(productCategoryCreateDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @DeleteMapping("/{productCategoryId}")
    @Operation(summary = "Deletes A ProductCategory")
    public ResponseEntity<Object> deleteById(@PathVariable int productCategoryId) {
        service.deleteById(productCategoryId);
        return ResponseEntity.noContent().build();
    }
}
