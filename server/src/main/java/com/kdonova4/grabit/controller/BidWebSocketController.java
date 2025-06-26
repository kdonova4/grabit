package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.BidService;
import com.kdonova4.grabit.domain.ProductService;
import com.kdonova4.grabit.security.AppUserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class BidWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final BidService service;
    private final ProductService productService;
    private final AppUserService appUserService;

    public BidWebSocketController(SimpMessagingTemplate messagingTemplate, BidService service, ProductService productService, AppUserService appUserService) {
        this.messagingTemplate = messagingTemplate;
        this.service = service;
        this.productService = productService;
        this.appUserService = appUserService;
    }

//    @MessageMapping("/bids/place")
//    public void handleBid(BidCreateDTO bidMessage) {
//        System.out.println("Received bid message: " + bidMessage);
//        Optional<AppUser> appUser = appUserService.findUserById(bidMessage.getUserId());
//        Optional<Product> product = productService.findById(bidMessage.getProductId());
//
//        if(appUser.isEmpty() || product.isEmpty()) {
//            System.out.println("USER OR PRODUCT NOT FOUND");
//            return;
//        }
//
//        Bid bid = new Bid(0, bidMessage.getBidAmount(), Timestamp.valueOf(LocalDateTime.now()), product.get(), appUser.get());
//
//        Result<Bid> result = service.create(bid);
//
//        if(result.isSuccess()) {
//            messagingTemplate.convertAndSend("topic/bids/" + bid.getProduct().getProductId(), result.getPayload());
//        } else {
//            System.out.println("RESULT FAILURE");
//        }
//    }
}
