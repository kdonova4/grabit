package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.OfferService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Bid;
import com.kdonova4.grabit.model.Offer;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.security.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Offer Controller", description = "Offer Operations")
@RequestMapping("/api/v1/offers")
public class OfferController {

    private final OfferService service;
    private final ProductService productService;
    private final AppUserService appUserService;

    public OfferController(OfferService service, ProductService productService, AppUserService appUserService) {
        this.service = service;
        this.productService = productService;
        this.appUserService = appUserService;
    }

    @GetMapping
    @Operation(summary = "Finds All Offers")
    public ResponseEntity<List<Offer>> findAll() {
        List<Offer> offers = service.findAll();

        return ResponseEntity.ok(offers);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Offer By User")
    public ResponseEntity<List<Offer>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Offer> offers = service.findByUser(appUser.get());

        return ResponseEntity.ok(offers);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Finds Offer By Product")
    public ResponseEntity<List<Offer>> findByProduct(@PathVariable int productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Offer> offers = service.findByProduct(product.get());

        return ResponseEntity.ok(offers);
    }

    @GetMapping("/user/{userId}/product/{productId}")
    @Operation(summary = "Finds Offer by User and Product")
    public ResponseEntity<Offer> findByUserAndProduct(@PathVariable int userId, @PathVariable int productId) {
        Optional<AppUser> user = appUserService.findUserById(userId);
        Optional<Product> product = productService.findById(productId);

        if(user.isEmpty() || product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Offer> offer = service.findByUserAndProduct(user.get(), product.get());

        if(offer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(offer.get());
    }

    @GetMapping("/product/{productId}/expire-after/{expireDateTime}")
    @Operation(summary = "Finds Offers by Product and Expire DateTime")
    public ResponseEntity<List<Offer>> findByProductAndExpireDateAfter(
            @PathVariable int productId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expireDateTime) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Offer> offers = service.findByProductAndExpireDateAfter(product.get(), expireDateTime);

        return offers != null && !offers.isEmpty()
                ? ResponseEntity.ok(offers)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{offerId}")
    @Operation(summary = "Finds A Offer By ID")
    public ResponseEntity<Offer> findById(@PathVariable int offerId) {
        Optional<Offer> offer = service.findById(offerId);

        if(offer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(offer.get());
    }

    @PostMapping
    @Operation(summary = "Creates A Offer")
    public ResponseEntity<Object> create(@RequestBody Offer offer) {
        Result<Offer> result = service.create(offer);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{offerId}")
    @Operation(summary = "Deletes An Offer")
    public ResponseEntity<Object> deleteById(@PathVariable int offerId) {
        service.deleteById(offerId);
        return ResponseEntity.noContent().build();
    }
}
