package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.AddressService;
import com.kdonova4.grabit.domain.OrderService;
import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Order;
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
@Tag(name = "Bid Controller", description = "Bid Operations")
@RequestMapping("/api/v1/bids")
public class OrderController {

    private final OrderService service;
    private final AppUserService appUserService;
    private final AddressService addressService;

    public OrderController(OrderService service, AppUserService appUserService, AddressService addressService) {
        this.service = service;
        this.appUserService = appUserService;
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Find All Orders")
    public ResponseEntity<List<Order>> findAll() {
        List<Order> orders = service.findAll();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Orders By User")
    public ResponseEntity<List<Order>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isPresent()) {
            List<Order> orders = service.findByUser(appUser.get());

            return ResponseEntity.ok(orders);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/shipping-address/{addressId}")
    @Operation(summary = "Finds Order By Shipping Address")
    public ResponseEntity<List<Order>> findByShippingAddress(@PathVariable int addressId) {
        Optional<Address> address = addressService.findById(addressId);

        if(address.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Order> orders = service.findByShippingAddress(address.get());

        return ResponseEntity.ok(orders);
    }

}
