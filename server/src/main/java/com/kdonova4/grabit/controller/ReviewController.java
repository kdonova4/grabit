package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.ReviewService;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.Review;
import com.kdonova4.grabit.model.Watchlist;
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
    public ResponseEntity<List<Review>> findAll() {
        List<Review> reviews = service.findAll();

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{postedBy}")
    @Operation(summary = "Finds Reviews Posted By User")
    public ResponseEntity<List<Review>> findByPostedBy(@PathVariable int postedBy) {
        Optional<AppUser> appUser = appUserService.findUserById(postedBy);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = service.findByPostedBy(appUser.get());

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Finds Reviews By Product")
    public ResponseEntity<List<Review>> findByProduct(@PathVariable int productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = service.findByProduct(product.get());

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("seller/{sellerId}")
    @Operation(summary = "Finds Reviews For Seller")
    public ResponseEntity<List<Review>> findBySeller(@PathVariable int sellerId) {
        Optional<AppUser> appUser = appUserService.findUserById(sellerId);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = service.findBySeller(appUser.get());

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}/seller/{sellerId}")
    @Operation(summary = "Finds Reviews For A Seller From A User")
    public ResponseEntity<List<Review>> findByPostedByAndSeller(@PathVariable int userId, @PathVariable int sellerId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);
        Optional<AppUser> seller = appUserService.findUserById(sellerId);

        if(appUser.isEmpty() || seller.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = service.findByPostedByAndSeller(appUser.get(), seller.get());

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}/product/{productId}")
    @Operation(summary = "Finds A Review For A Product From A User")
    public ResponseEntity<Review> findByPostedByAndProduct(@PathVariable int userId, @PathVariable int productId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);
        Optional<Product> product = productService.findById(productId);

        if(appUser.isEmpty() || product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Review> review = service.findByPostedByAndProduct(appUser.get(), product.get());

        if(review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(review.get());
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Finds A Review By ID")
    public ResponseEntity<Review> findById(@PathVariable int reviewId) {
        Optional<Review> review = service.findById(reviewId);

        if(review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(review.get());
    }

    @PostMapping
    @Operation(summary = "Creates A Review")
    public ResponseEntity<Object> create(@RequestBody Review review) {
        Result<Review> result = service.create(review);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Updates A Review")
    public ResponseEntity<Object> update(@PathVariable int reviewId, @RequestBody Review review) {
        if(reviewId != review.getReviewId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Review> result = service.update(review);

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
