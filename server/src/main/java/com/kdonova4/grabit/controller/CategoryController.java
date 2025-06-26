package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.CategoryService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.model.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Category Controller", description = "Category Operations")
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Finds All Categories")
    public ResponseEntity<List<Category>> findAll() {
        List<Category> categories = service.findAll();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/category-name/{name}")
    @Operation(summary = "Finds The Category By Name")
    public ResponseEntity<Category> findByCategoryName(@PathVariable String name) {
        Optional<Category> category = service.findByCategoryName(name);

        if(category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category.get());
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Finds A Category By ID")
    public ResponseEntity<Category> findById(@PathVariable int categoryId) {
        Optional<Category> category = service.findById(categoryId);

        if(category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category.get());
    }

    @PostMapping
    @Operation(summary = "Creates A Category")
    public ResponseEntity<Object> create(@RequestBody Category category) {
        Result<Category> result = service.create(category);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Deletes A Category")
    public ResponseEntity<Object> deleteById(@PathVariable int categoryId) {
        service.deleteById(categoryId);
        return ResponseEntity.noContent().build();
    }
}
