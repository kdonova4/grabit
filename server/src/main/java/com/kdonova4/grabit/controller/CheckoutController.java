package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.CheckoutService;
import com.kdonova4.grabit.model.dto.CheckoutRequestDTO;
import com.kdonova4.grabit.model.dto.CheckoutResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Checkout Controller", description = "Checkout Operations")
@RequestMapping("/api/v1/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    @Operation(summary = "Checkout and Create Order, Shipment, and Payment")
    public ResponseEntity<CheckoutResponseDTO> checkout(@RequestBody CheckoutRequestDTO checkoutRequestDTO) {
        return ResponseEntity.ok(checkoutService.checkout(checkoutRequestDTO));
    }
}
