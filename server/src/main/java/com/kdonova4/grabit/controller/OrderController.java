package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.AddressService;
import com.kdonova4.grabit.domain.OrderService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.mapper.OrderMapper;
import com.kdonova4.grabit.domain.mapper.OrderProductMapper;
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
    public ResponseEntity<List<OrderResponseDTO>> findAll() {
        List<Order> orders = service.findAll();

        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).toList());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Orders By User")
    public ResponseEntity<List<OrderResponseDTO>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isPresent()) {
            List<Order> orders = service.findByUser(appUser.get());

            return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).toList());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/shipping-address/{addressId}")
    @Operation(summary = "Finds Order By Shipping Address")
    public ResponseEntity<List<OrderResponseDTO>> findByShippingAddress(@PathVariable int addressId) {
        Optional<Address> address = addressService.findById(addressId);

        if(address.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Order> orders = service.findByShippingAddress(address.get());

        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).toList());
    }

    @GetMapping("/billing-address/{addressId}")
    @Operation(summary = "Finds Order By Billing Address")
    public ResponseEntity<List<OrderResponseDTO>> findByBillingAddress(@PathVariable int addressId) {
        Optional<Address> address = addressService.findById(addressId);

        if(address.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Order> orders = service.findByBillingAddress(address.get());

        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).toList());
    }

    @GetMapping("/order-after/{expireTimestamp}")
    @Operation(summary = "Finds Orders After Timestamp")
    public ResponseEntity<List<OrderResponseDTO>> findByOrderedAtAfter(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

        Timestamp timestamp = Timestamp.valueOf(time);

        List<Order> orders = service.findByOrderedAtAfter(timestamp);

        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).toList());
    }

    @GetMapping("/order-between/{start}/{end}")
    @Operation(summary = "Finds Orders After Timestamp")
    public ResponseEntity<List<OrderResponseDTO>> findByOrderedAtBetween(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Timestamp timestampStart = Timestamp.valueOf(start);
        Timestamp timestampEnd = Timestamp.valueOf(end);

        List<Order> orders = service.findByOrderedAtBetween(timestampStart, timestampEnd);

        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).toList());
    }

    @GetMapping("amount/greater-than/{amount}")
    @Operation(summary = "Finds Orders That Are Greater Than Amount")
    public ResponseEntity<List<OrderResponseDTO>> findByTotalAmountGreaterThan(@PathVariable BigDecimal amount) {
        List<Order> orders = service.findByTotalAmountGreaterThan(amount);

        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).toList());
    }

    @GetMapping("amount/less-than/{amount}")
    @Operation(summary = "Finds Orders That Are Less Than Amount")
    public ResponseEntity<List<OrderResponseDTO>> findByTotalAmountLessThan(@PathVariable BigDecimal amount) {
        List<Order> orders = service.findByTotalAmountLessThan(amount);

        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).toList());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Finds An Order By ID")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable int orderId) {
        Optional<Order> order = service.findById(orderId);

        if(order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrderMapper.toResponse(order.get()));
    }

}
