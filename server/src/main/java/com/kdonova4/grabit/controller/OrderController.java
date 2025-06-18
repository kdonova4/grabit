package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.AddressService;
import com.kdonova4.grabit.domain.OrderService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.model.*;
import com.kdonova4.grabit.security.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Order Controller", description = "Order Operations")
@RequestMapping("/api/v1/orders")
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

    @GetMapping("/billing-address/{addressId}")
    @Operation(summary = "Finds Order By Billing Address")
    public ResponseEntity<List<Order>> findByBillingAddress(@PathVariable int addressId) {
        Optional<Address> address = addressService.findById(addressId);

        if(address.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Order> orders = service.findByBillingAddress(address.get());

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/order-after/{expireTimestamp}")
    @Operation(summary = "Finds Orders After Timestamp")
    public ResponseEntity<List<Order>> findByOrderedAtAfter(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

        Timestamp timestamp = Timestamp.valueOf(time);

        List<Order> orders = service.findByOrderedAtAfter(timestamp);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/order-between/{start}/{end}")
    @Operation(summary = "Finds Orders After Timestamp")
    public ResponseEntity<List<Order>> findByOrderedAtBetween(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Timestamp timestampStart = Timestamp.valueOf(start);
        Timestamp timestampEnd = Timestamp.valueOf(end);

        List<Order> orders = service.findByOrderedAtBetween(timestampStart, timestampEnd);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("amount/greater-than/{amount}")
    @Operation(summary = "Finds Orders That Are Greater Than Amount")
    public ResponseEntity<List<Order>> findByTotalAmountGreaterThan(@PathVariable BigDecimal amount) {
        List<Order> orders = service.findByTotalAmountGreaterThan(amount);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("amount/less-than/{amount}")
    @Operation(summary = "Finds Orders That Are Less Than Amount")
    public ResponseEntity<List<Order>> findByTotalAmountLessThan(@PathVariable BigDecimal amount) {
        List<Order> orders = service.findByTotalAmountLessThan(amount);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Finds An Order By ID")
    public ResponseEntity<Order> findById(@PathVariable int orderId) {
        Optional<Order> order = service.findById(orderId);

        if(order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order.get());
    }

    @PostMapping
    @Operation(summary = "Creates An Order")
    public ResponseEntity<Object> create(@RequestBody CheckoutRequest request) {
        Result<Order> result = service.create(request);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }
}
