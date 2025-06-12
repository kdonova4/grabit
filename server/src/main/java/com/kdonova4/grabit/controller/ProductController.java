package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.CategoryService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Category;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.Review;
import com.kdonova4.grabit.security.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    @Operation(summary = "Finds All Products")
    public ResponseEntity<List<Product>> findAll() {
        List<Product> products = service.findAll();

        return ResponseEntity.ok(products);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Products By User")
    public ResponseEntity<List<Product>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Product> products = service.findByUser(appUser.get());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(summary = "Finds By Search")
    public ResponseEntity<List<Product>> search(@RequestParam String productName,
                                                @RequestParam BigDecimal minPrice,
                                                @RequestParam BigDecimal maxPrice,
                                                @RequestParam ProductStatus status,
                                                @RequestParam ConditionType condition,
                                                @RequestParam SaleType saleType,
                                                @RequestParam int categoryId) {

        Category category = categoryService.findById(categoryId).orElse(null);

        List<Product> products = service.search(productName, minPrice, maxPrice, status, condition, saleType, category);

        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Creates A Product")
    public ResponseEntity<Object> create(@RequestBody Product product) {
        Result<Product> result = service.create(product);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Updates A Product")
    public ResponseEntity<Object> update(@PathVariable int productId, @RequestBody Product product) {
        if(productId != product.getProductId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Product> result = service.update(product);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Deletes A Product")
    public ResponseEntity<Object> deleteById(@PathVariable int reviewId) {
        service.deleteById(reviewId);
        return ResponseEntity.noContent().build();
    }
}
