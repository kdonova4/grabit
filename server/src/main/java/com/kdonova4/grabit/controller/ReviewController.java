package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.ReviewService;
import com.kdonova4.grabit.domain.mapper.ReviewMapper;
import com.kdonova4.grabit.model.*;
import com.kdonova4.grabit.security.AppUserService;
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
@Tag(name = "Review Controller", description = "Reviews Operations")
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService service;
    private final ProductService productService;
    private final AppUserService appUserService;

    public ReviewController(ReviewService service, ProductService productService, AppUserService appUserService) {
        this.service = service;
        this.productService = productService;
        this.appUserService = appUserService;
    }

    @GetMapping
    @Operation(summary = "Finds All Reviews")
    public ResponseEntity<List<ReviewResponseDTO>> findAll() {
        List<Review> reviews = service.findAll();

        return ResponseEntity.ok(reviews.stream().map(ReviewMapper::toResponseDTO).toList());
    }

    @GetMapping("/user/{postedBy}")
    @Operation(summary = "Finds Reviews Posted By User")
    public ResponseEntity<List<ReviewResponseDTO>> findByPostedBy(@PathVariable int postedBy) {
        Optional<AppUser> appUser = appUserService.findUserById(postedBy);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = service.findByPostedBy(appUser.get());

        return ResponseEntity.ok(reviews.stream().map(ReviewMapper::toResponseDTO).toList());
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Finds Reviews By Product")
    public ResponseEntity<List<ReviewResponseDTO>> findByProduct(@PathVariable int productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = service.findByProduct(product.get());

        return ResponseEntity.ok(reviews.stream().map(ReviewMapper::toResponseDTO).toList());
    }

    @GetMapping("seller/{sellerId}")
    @Operation(summary = "Finds Reviews For Seller")
    public ResponseEntity<List<ReviewResponseDTO>> findBySeller(@PathVariable int sellerId) {
        Optional<AppUser> appUser = appUserService.findUserById(sellerId);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = service.findBySeller(appUser.get());

        return ResponseEntity.ok(reviews.stream().map(ReviewMapper::toResponseDTO).toList());
    }

    @GetMapping("/user/{userId}/seller/{sellerId}")
    @Operation(summary = "Finds Reviews For A Seller From A User")
    public ResponseEntity<List<ReviewResponseDTO>> findByPostedByAndSeller(@PathVariable int userId, @PathVariable int sellerId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);
        Optional<AppUser> seller = appUserService.findUserById(sellerId);

        if(appUser.isEmpty() || seller.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = service.findByPostedByAndSeller(appUser.get(), seller.get());

        return ResponseEntity.ok(reviews.stream().map(ReviewMapper::toResponseDTO).toList());
    }

    @GetMapping("/user/{userId}/product/{productId}")
    @Operation(summary = "Finds A Review For A Product From A User")
    public ResponseEntity<ReviewResponseDTO> findByPostedByAndProduct(@PathVariable int userId, @PathVariable int productId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);
        Optional<Product> product = productService.findById(productId);

        if(appUser.isEmpty() || product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Review> review = service.findByPostedByAndProduct(appUser.get(), product.get());

        if(review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ReviewMapper.toResponseDTO(review.get()));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Finds A Review By ID")
    public ResponseEntity<ReviewResponseDTO> findById(@PathVariable int reviewId) {
        Optional<Review> review = service.findById(reviewId);

        if(review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ReviewMapper.toResponseDTO(review.get()));
    }

    @PostMapping
    @Operation(summary = "Creates A Review")
    public ResponseEntity<Object> create(@RequestBody ReviewCreateDTO reviewCreateDTO) {
        Result<ReviewResponseDTO> result = service.create(reviewCreateDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Updates A Review")
    public ResponseEntity<Object> update(@PathVariable int reviewId, @RequestBody ReviewUpdateDTO reviewUpdateDTO) {
        if(reviewId != reviewUpdateDTO.getReviewId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<ReviewResponseDTO> result = service.update(reviewUpdateDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Deletes A Review")
    public ResponseEntity<Object> deleteById(@PathVariable int reviewId) {
        service.deleteById(reviewId);
        return ResponseEntity.noContent().build();
    }
}
