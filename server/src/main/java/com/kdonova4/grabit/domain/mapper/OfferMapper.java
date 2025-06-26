package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.enums.OfferStatus;
import com.kdonova4.grabit.model.dto.OfferCreateDTO;
import com.kdonova4.grabit.model.dto.OfferResponseDTO;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Offer;
import com.kdonova4.grabit.model.entity.Product;

public class OfferMapper {

    public static Offer toOffer(OfferCreateDTO offerCreateDTO, AppUser user, Product product) {
        return new Offer(
                0,
                offerCreateDTO.getOfferAmount(),
                null,
                offerCreateDTO.getMessage(),
                user,
                product,
                null,
                OfferStatus.PENDING
        );
    }

    public static OfferResponseDTO toResponseDTO(Offer offer) {
        return new OfferResponseDTO(
                offer.getOfferId(),
                offer.getOfferAmount(),
                offer.getSentAt(),
                offer.getMessage(),
                offer.getUser().getAppUserId(),
                offer.getProduct().getProductId(),
                offer.getExpireDate(),
                offer.getOfferStatus()
        );
    }
}
