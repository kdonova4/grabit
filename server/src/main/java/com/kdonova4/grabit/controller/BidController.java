package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.BidService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.model.*;
import com.kdonova4.grabit.security.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bid Controller", description = "Bid Operations")
@RequestMapping("/api/v1/bids")
public class BidController {

    private final BidService service;
    private final ProductService productService;
    private final AppUserService appUserService;
    private final SimpMessagingTemplate messagingTemplate;
    public BidController(BidService service, ProductService productService, AppUserService appUserService, SimpMessagingTemplate messagingTemplate) {
        this.service = service;
        this.productService = productService;
        this.appUserService = appUserService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    @Operation(summary = "Finds All Bids")
    public ResponseEntity<List<Bid>> findAll() {
        List<Bid> bids = service.findAll();

        return ResponseEntity.ok(bids);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Bid By User")
    public ResponseEntity<List<Bid>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Bid> bids = service.findByUser(appUser.get());

        return ResponseEntity.ok(bids);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Finds Bid By Product")
    public ResponseEntity<List<Bid>> findByProduct(@PathVariable int productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Bid> bids = service.findByProduct(product.get());

        return ResponseEntity.ok(bids);
    }

    @GetMapping("/{bidId}")
    @Operation(summary = "Finds A Bid By ID")
    public ResponseEntity<Bid> findById(@PathVariable int bidId) {
        Optional<Bid> bid = service.findById(bidId);

        if(bid.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bid.get());
    }

    @PostMapping
    @Operation(summary = "Creates A Bid")
    public ResponseEntity<Object> create(@RequestBody Bid bid) {
        Result<Bid> result = service.create(bid);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        List<Bid> updatedBids = service.findByProductOrderByBidAmountDesc(bid.getProduct());

        List<BidMessage> bidMessages = updatedBids.stream()
                        .map(b -> new BidMessage(
                                b.getUser().getAppUserId(),
                                b.getProduct().getProductId(),
                                b.getBidAmount()
                        ))
                                .toList();


        messagingTemplate.convertAndSend("/topic/bids/" + bid.getProduct().getProductId(), bidMessages);
        System.out.println("SENT MESSAGE WEBSOCKET");

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{bidId}")
    @Operation(summary = "Deletes A Bid")
    public ResponseEntity<Object> deleteById(@PathVariable int bidId) {
        service.deleteById(bidId);
        return ResponseEntity.noContent().build();
    }
}
