package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.OrderProductService;
import com.kdonova4.grabit.domain.OrderService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.mapper.OrderMapper;
import com.kdonova4.grabit.domain.mapper.OrderProductMapper;
import com.kdonova4.grabit.model.*;
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
@Tag(name = "OrderProduct Controller", description = "OrderProduct Operations")
@RequestMapping("/api/v1/order-products")
public class OrderProductController {

    private final OrderProductService service;
    private final ProductService productService;
    private final OrderService orderService;

    public OrderProductController(OrderProductService service, ProductService productService, OrderService orderService) {
        this.service = service;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Finds OrderProducts By Order")
    public ResponseEntity<List<OrderProductResponseDTO>> findByOrder(@PathVariable int orderId) {
        Optional<Order> order = orderService.findById(orderId);

        if(order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<OrderProduct> orderProducts = service.findByOrder(order.get());

        return ResponseEntity.ok(OrderProductMapper.toDTO(orderProducts));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Finds OrderProducts By Product")
    public ResponseEntity<List<OrderProductResponseDTO>> findByProduct(@PathVariable int productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<OrderProduct> orderProducts = service.findByProduct(product.get());

        return ResponseEntity.ok(OrderProductMapper.toDTO(orderProducts));
    }

    @GetMapping("/{orderProductId}")
    @Operation(summary = "Finds An OrderProduct By ID")
    public ResponseEntity<OrderProductResponseDTO> findById(@PathVariable int orderProductId) {
        Optional<OrderProduct> orderProduct = service.findById(orderProductId);

        if(orderProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrderProductMapper.toDTO(orderProduct.get()));
    }

    @PostMapping
    @Operation(summary = "Creates A OrderProduct")
    public ResponseEntity<Object> create(@RequestBody OrderProductCreateDTO orderProduct) {
        Result<OrderProductResponseDTO> result = service.create(orderProduct);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }


}
