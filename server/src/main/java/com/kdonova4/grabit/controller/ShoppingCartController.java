package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.ShoppingCartService;
import com.kdonova4.grabit.domain.mapper.ShoppingCartMapper;
import com.kdonova4.grabit.model.dto.ShoppingCartDTO;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ShoppingCart;
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
@Tag(name = "ShoppingCart Controller", description = "ShoppingCart Operations")
@RequestMapping("/api/v1/carts")
public class ShoppingCartController {

    private final ShoppingCartService service;
    private final ProductService productService;
    private final AppUserService appUserService;

    public ShoppingCartController(ShoppingCartService service, ProductService productService, AppUserService appUserService) {
        this.service = service;
        this.productService = productService;
        this.appUserService = appUserService;
    }

    @GetMapping
    @Operation(summary = "Finds All Shopping Carts")
    public ResponseEntity<List<ShoppingCartDTO>> findAll() {
        List<ShoppingCart> shoppingCarts = service.findAll();

        return ResponseEntity.ok(shoppingCarts.stream().map(ShoppingCartMapper::toResponseDTO).toList());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Shopping Cart Items By User")
    public ResponseEntity<List<ShoppingCartDTO>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isPresent()) {
            List<ShoppingCart> cartList = service.findByUser(appUser.get());

            return ResponseEntity.ok(cartList.stream().map(ShoppingCartMapper::toResponseDTO).toList());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}/product/{productId}")
    @Operation(summary = "Finds Shopping Cart Items By User And Product")
    public ResponseEntity<ShoppingCartDTO> findByUserProduct(@PathVariable int userId, @PathVariable int productId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);
        Optional<Product> product = productService.findById(productId);

        if(appUser.isEmpty() || product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<ShoppingCart> shoppingCart = service.findByUserAndProduct(appUser.get(), product.get());

        if(shoppingCart.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ShoppingCartMapper.toResponseDTO(shoppingCart.get()));
    }

    @GetMapping("/{cartId}")
    @Operation(summary = "Finds A Cart Item By ID")
    public ResponseEntity<ShoppingCartDTO> findById(@PathVariable int cartId) {
        Optional<ShoppingCart> cart = service.findById(cartId);

        if(cart.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ShoppingCartMapper.toResponseDTO(cart.get()));
    }

    @PostMapping
    @Operation(summary = "Creates A ShoppingCart Item")
    public ResponseEntity<Object> create(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        Result<ShoppingCartDTO> result = service.create(shoppingCartDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{cartId}")
    @Operation(summary = "Updates A Product")
    public ResponseEntity<Object> update(@PathVariable int cartId, @RequestBody ShoppingCartDTO shoppingCartDTO) {
        if(cartId != shoppingCartDTO.getShoppingCartId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<ShoppingCartDTO> result = service.update(shoppingCartDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{cartId}")
    @Operation(summary = "Deletes A Cart Item")
    public ResponseEntity<Object> deleteById(@PathVariable int cartId) {
        service.deleteById(cartId);
        return ResponseEntity.noContent().build();
    }
}
