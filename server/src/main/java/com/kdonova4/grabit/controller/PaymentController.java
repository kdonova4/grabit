package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.OrderService;
import com.kdonova4.grabit.domain.PaymentService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.mapper.PaymentMapper;
import com.kdonova4.grabit.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment Controller", description = "Payment Operations")
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService service;
    private final OrderService orderService;

    public PaymentController(PaymentService service, OrderService orderService) {
        this.service = service;
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Finds All Payments")
    public ResponseEntity<List<PaymentResponseDTO>> findAll() {
        List<Payment> payments = service.findAll();

        return ResponseEntity.ok(payments.stream().map(PaymentMapper::toResponse).toList());
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Finds A Payment By An Order")
    public ResponseEntity<PaymentResponseDTO> findByOrder(@PathVariable int orderId) {
        Optional<Order> order = orderService.findById(orderId);

        if(order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Payment> payment = service.findByOrder(order.get());

        if(payment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PaymentMapper.toResponse(payment.get()));
    }

    @GetMapping("amount/greater-than/{amount}")
    @Operation(summary = "Finds Payments That Are Greater Than Amount")
    public ResponseEntity<List<PaymentResponseDTO>> findByAmountPaidGreaterThan(@PathVariable BigDecimal amount) {
        List<Payment> payments = service.findByAmountPaidGreaterThan(amount);

        return ResponseEntity.ok(payments.stream().map(PaymentMapper::toResponse).toList());
    }

    @GetMapping("amount/less-than/{amount}")
    @Operation(summary = "Finds Payments That Are Less Than Amount")
    public ResponseEntity<List<PaymentResponseDTO>> findByAmountPaidLessThan(@PathVariable BigDecimal amount) {
        List<Payment> payments = service.findByAmountPaidLessThan(amount);

        return ResponseEntity.ok(payments.stream().map(PaymentMapper::toResponse).toList());
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Finds A Payment By ID")
    public ResponseEntity<PaymentResponseDTO> findById(@PathVariable int paymentId) {
        Optional<Payment> payment = service.findById(paymentId);

        if(payment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PaymentMapper.toResponse(payment.get()));
    }

    @PostMapping
    @Operation(summary = "Creates A Payment")
    public ResponseEntity<Object> create(@RequestBody PaymentCreateDTO paymentCreateDTO) {
        Result<PaymentResponseDTO> result = service.create(paymentCreateDTO);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }


}
