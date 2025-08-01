package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.CategoryService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.mapper.ProductMapper;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.dto.ProductCreateDTO;
import com.kdonova4.grabit.model.dto.ProductResponseDTO;
import com.kdonova4.grabit.model.dto.ProductUpdateDTO;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.security.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Product Controller", description = "Product Operations")
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService service;
    private final AppUserService appUserService;
    private final CategoryService categoryService;

    public ProductController(ProductService service, AppUserService appUserService, CategoryService categoryService) {
        this.service = service;
        this.appUserService = appUserService;
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Finds All Products")
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        List<Product> products = service.findAll();

        return ResponseEntity.ok(products.stream().map(ProductMapper::toResponseDTO).toList());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Products By User")
    public ResponseEntity<List<ProductResponseDTO>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Product> products = service.findByUser(appUser.get());

        return ResponseEntity.ok(products.stream().map(ProductMapper::toResponseDTO).toList());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Finds products by category")
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(@PathVariable int categoryId) {

        List<Product> products = service.findAllByCategoryId(categoryId);

        return ResponseEntity.ok(products.stream().map(ProductMapper::toResponseDTO).toList());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Finds A Product By ID")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable int productId) {
        Optional<Product> product = service.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ProductMapper.toResponseDTO(product.get()));
    }

    @GetMapping("/search")
    @Operation(summary = "Finds By Search")
    public ResponseEntity<List<ProductResponseDTO>> search(@RequestParam(required = false) String productName,
                                                @RequestParam(required = false) BigDecimal minPrice,
                                                @RequestParam(required = false) BigDecimal maxPrice,
                                                @RequestParam(required = false) ProductStatus status,
                                                @RequestParam(required = false) ConditionType condition,
                                                @RequestParam(required = false) SaleType saleType,
                                                @RequestParam(required = false) Integer categoryId) {

        Category category = (categoryId != null)
                ? categoryService.findById(categoryId).orElse(null)
                : null;

        List<Product> products = service.search(productName, minPrice, maxPrice, status, condition, saleType, category);

        return ResponseEntity.ok(products.stream().map(ProductMapper::toResponseDTO).toList());
    }

    @PreAuthorize("hasAnyRole('SELLER')")
    @PostMapping
    @Operation(summary = "Creates A Product")
    public ResponseEntity<Object> create(@RequestBody ProductCreateDTO productCreateDTO) {
        Result<ProductResponseDTO> result = service.create(productCreateDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('SELLER')")
    @PutMapping("/{productId}")
    @Operation(summary = "Updates A Product")
    public ResponseEntity<Object> update(@PathVariable int productId, @RequestBody ProductUpdateDTO productUpdateDTO) {
        if(productId != productUpdateDTO.getProductId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<ProductResponseDTO> result = service.update(productUpdateDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @DeleteMapping("/{productId}")
    @Operation(summary = "Deletes A Product")
    public ResponseEntity<Object> deleteById(@PathVariable int productId) {
        service.deleteById(productId);
        return ResponseEntity.noContent().build();
    }
}
