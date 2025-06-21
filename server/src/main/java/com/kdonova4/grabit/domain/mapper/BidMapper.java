package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.*;

public class BidMapper {

    public static Bid toBid(BidCreateDTO bidCreateDTO, Product product, AppUser user) {
        return new Bid(
                0,
                bidCreateDTO.getBidAmount(),
                null,
                product,
                user
        );
    }

    public static BidResponseDTO toResponseDTO(Bid bid) {
        return new BidResponseDTO(
                bid.getBidId(),
                bid.getBidAmount(),
                bid.getPlacedAt(),
                bid.getProduct().getProductId(),
                bid.getUser().getAppUserId()
        );
    }
}
