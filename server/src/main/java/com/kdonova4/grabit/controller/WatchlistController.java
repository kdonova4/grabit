package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.WatchlistService;
import com.kdonova4.grabit.domain.mapper.WatchlistMapper;
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
@Tag(name = "Watchlist Controller", description = "Watchlist Operations")
@RequestMapping("/api/v1/watchlists")
public class WatchlistController {

    private final WatchlistService service;
    private final AppUserService appUserService;
    private final ProductService productService;

    public WatchlistController(WatchlistService service, AppUserService appUserService, ProductService productService) {
        this.service = service;
        this.appUserService = appUserService;
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Finds All Watchlist Items")
    public ResponseEntity<List<WatchlistDTO>> findAll() {
        List<Watchlist> list = service.findAll();

        return ResponseEntity.ok(list.stream().map(WatchlistMapper::toDTO).toList());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Watchlist Item By User")
    public ResponseEntity<List<WatchlistDTO>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isPresent()) {
            List<Watchlist> list = service.findByUser(appUser.get());

            return ResponseEntity.ok(list.stream().map(WatchlistMapper::toDTO).toList());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}/product/{productId}")
    @Operation(summary = "Finds Watchlist Items By User And Product")
    public ResponseEntity<WatchlistDTO> findByUserProduct(@PathVariable int userId, @PathVariable int productId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);
        Optional<Product> product = productService.findById(productId);

        if(appUser.isEmpty() || product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Watchlist> watchlist = service.findByUserAndProduct(appUser.get(), product.get());

        if(watchlist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(WatchlistMapper.toDTO(watchlist.get()));
    }

    @GetMapping("/{watchId}")
    @Operation(summary = "Finds A Watchlist Item By ID")
    public ResponseEntity<WatchlistDTO> findById(@PathVariable int watchId) {
        Optional<Watchlist> watchlist = service.findById(watchId);

        if(watchlist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(WatchlistMapper.toDTO(watchlist.get()));
    }

    @PostMapping
    @Operation(summary = "Creates A Watchlist Item")
    public ResponseEntity<Object> create(@RequestBody WatchlistDTO watchlistDTO) {
        Result<WatchlistDTO> result = service.create(watchlistDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{watchId}")
    @Operation(summary = "Deletes A Watchlist Item")
    public ResponseEntity<Object> deleteById(@PathVariable int watchId) {
        service.deleteById(watchId);
        return ResponseEntity.noContent().build();
    }
}
