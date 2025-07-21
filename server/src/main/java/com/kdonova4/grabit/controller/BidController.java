package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.BidService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.mapper.BidMapper;
import com.kdonova4.grabit.model.dto.BidCreateDTO;
import com.kdonova4.grabit.model.dto.BidResponseDTO;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.AppUserDetails;
import com.kdonova4.grabit.model.entity.Bid;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.security.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<BidResponseDTO>> findAll() {
        List<Bid> bids = service.findAll();

        return ResponseEntity.ok(bids.stream().map(BidMapper::toResponseDTO).toList());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Finds Bid By User")
    public ResponseEntity<List<BidResponseDTO>> findByUser(@PathVariable int userId) {
        Optional<AppUser> appUser = appUserService.findUserById(userId);

        if(appUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Bid> bids = service.findByUser(appUser.get());

        return ResponseEntity.ok(bids.stream().map(BidMapper::toResponseDTO).toList());
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Finds Bid By Product")
    public ResponseEntity<List<BidResponseDTO>> findByProduct(@PathVariable int productId) {
        Optional<Product> product = productService.findById(productId);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Bid> bids = service.findByProduct(product.get());

        return ResponseEntity.ok(bids.stream().map(BidMapper::toResponseDTO).toList());
    }

    @GetMapping("/{bidId}")
    @Operation(summary = "Finds A Bid By ID")
    public ResponseEntity<BidResponseDTO> findById(@PathVariable int bidId) {
        Optional<Bid> bid = service.findById(bidId);

        if(bid.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(BidMapper.toResponseDTO(bid.get()));
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping
    @Operation(summary = "Creates A Bid")
    public ResponseEntity<Object> create(@RequestBody BidCreateDTO bid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        System.out.println(username);
        AppUserDetails userDetails = (AppUserDetails) appUserService.loadUserByUsername(username);
        AppUser  user = userDetails.getAppUser();
        int authenticatedUserId = user.getAppUserId();

        if(bid.getUserId() != authenticatedUserId) {
            return new ResponseEntity<>("Not authorized to create a review for another user", HttpStatus.FORBIDDEN);
        }

        Result<BidResponseDTO> result = service.create(bid);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }



        Bid actual = BidMapper.toBid(bid, productService.findById(bid.getProductId()).get(), appUserService.findUserById(bid.getUserId()).get());

        List<Bid> updatedBids = service.findByProductOrderByBidAmountDesc(actual.getProduct());

        List<BidResponseDTO> bidResponseDTOS = updatedBids.stream()
                        .map(b -> new BidResponseDTO(
                                b.getBidId(),
                                b.getBidAmount(),
                                b.getPlacedAt(),
                                b.getProduct().getProductId(),
                                b.getUser().getAppUserId()
                        ))
                                .toList();


        messagingTemplate.convertAndSend("/topic/bids/" + actual.getProduct().getProductId(), bidResponseDTOS);
        System.out.println("SENT MESSAGE WEBSOCKET");

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{bidId}")
    @Operation(summary = "Deletes A Bid")
    public ResponseEntity<Object> deleteById(@PathVariable int bidId) {

        Optional<Bid> bid = service.findById(bidId);

        if(service.deleteById(bidId) && bid.isPresent()) {
            List<Bid> updatedBids = service.findByProductOrderByBidAmountDesc(bid.get().getProduct());

            List<BidResponseDTO> bidResponseDTOS = updatedBids.stream()
                    .map(b -> new BidResponseDTO(
                            b.getBidId(),
                            b.getBidAmount(),
                            b.getPlacedAt(),
                            b.getProduct().getProductId(),
                            b.getUser().getAppUserId()
                    ))
                    .toList();


            messagingTemplate.convertAndSend("/topic/bids/" + bid.get().getProduct().getProductId(), bidResponseDTOS);
            System.out.println("SENT MESSAGE WEBSOCKET");
        }

        return ResponseEntity.noContent().build();
    }
}
