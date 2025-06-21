package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.ImageService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.mapper.ImageMapper;
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
@Tag(name = "Image Controller", description = "Image Operations")
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService service;
    private final ProductService productService;

    public ImageController(ImageService service, ProductService productService) {
        this.service = service;
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Finds All Images")
    public ResponseEntity<List<ImageResponseDTO>> findAll() {
        List<Image> images = service.findAll();

        return ResponseEntity.ok(images.stream().map(ImageMapper::toResponseDTO).toList());
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Finds Images Associated With A Product")
    public ResponseEntity<List<ImageResponseDTO>> findByProduct(@PathVariable int productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isPresent()) {
            List<Image> images = service.findByProduct(product.get());

            return ResponseEntity.ok(images.stream().map(ImageMapper::toResponseDTO).toList());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{imageId}")
    @Operation(summary = "Finds A Image By ID")
    public ResponseEntity<ImageResponseDTO> findById(@PathVariable int imageId) {
        Optional<Image> image = service.findById(imageId);

        if(image.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ImageMapper.toResponseDTO(image.get()));
    }

    @PostMapping
    @Operation(summary = "Creates An Image")
    public ResponseEntity<Object> create(@RequestBody ImageCreateDTO image) {
        Result<ImageResponseDTO> result = service.create(image);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Deletes A Image")
    public ResponseEntity<Object> deleteById(@PathVariable int imageId) {
        service.deleteById(imageId);
        return ResponseEntity.noContent().build();
    }


}
