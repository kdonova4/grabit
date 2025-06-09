package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.ShoppingCartService;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.ShoppingCart;
import com.kdonova4.grabit.security.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<List<ShoppingCart>> findAll() {
        List<ShoppingCart> shoppingCarts = service.findAll();

        return ResponseEntity.ok(shoppingCarts);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Shopping Cart Items By User")
    public ResponseEntity<List<ShoppingCart>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isPresent()) {
            List<ShoppingCart> cartList = service.findByUser(appUser.get());

            return ResponseEntity.ok(cartList);
        }

        return ResponseEntity.notFound().build();
    }
}
