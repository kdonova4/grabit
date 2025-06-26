package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.OrderService;
import com.kdonova4.grabit.domain.ShipmentService;
import com.kdonova4.grabit.domain.mapper.ShipmentMapper;
import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.Shipment;
import com.kdonova4.grabit.model.dto.ShipmentResponseDTO;
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
@Tag(name = "Shipment Controller", description = "Shipment Operations")
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService service;
    private final OrderService orderService;

    public ShipmentController(ShipmentService service, OrderService orderService) {
        this.service = service;
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Finds All Shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> findAll() {
        List<Shipment> shipments = service.findAll();

        return ResponseEntity.ok(shipments.stream().map(ShipmentMapper::toResponse).toList());
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Finds A Shipment By An Order")
    public ResponseEntity<ShipmentResponseDTO> findByOrder(@PathVariable int orderId) {
        Optional<Order> order = orderService.findById(orderId);

        if(order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Shipment> shipment = service.findByOrder(order.get());

        if(shipment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ShipmentMapper.toResponse(shipment.get()));
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Finds Shipment By Tracking Number")
    public ResponseEntity<ShipmentResponseDTO> findByTrackingNumber(@PathVariable String trackingNumber) {
        Optional<Shipment> shipment = service.findByTrackingNumber(trackingNumber);

        if(shipment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ShipmentMapper.toResponse(shipment.get()));
    }

    @GetMapping("/{shipmentId}")
    @Operation(summary = "Finds A Shipment By ID")
    public ResponseEntity<ShipmentResponseDTO> findById(@PathVariable int shipmentId) {
        Optional<Shipment> shipment = service.findById(shipmentId);

        if(shipment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ShipmentMapper.toResponse(shipment.get()));
    }



}
