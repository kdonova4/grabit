package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.*;

public class ReviewMapper {

    public static ReviewResponseDTO toResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getReviewId(),
                review.getRating(),
                review.getReviewText(),
                review.getPostedBy().getAppUserId(),
                review.getSeller().getAppUserId(),
                review.getProduct().getProductId(),
                review.getCreatedAt()
        );
    }

    public static Review toReview(ReviewCreateDTO reviewCreateDTO, AppUser postedBy, AppUser seller, Product product) {
        return new Review(
                0,
                reviewCreateDTO.getRating(),
                reviewCreateDTO.getReviewText(),
                postedBy,
                seller,
                product,
                null
        );
    }

    public static Review toReview(ReviewUpdateDTO reviewUpdateDTO, Review review) {
        return new Review(
                reviewUpdateDTO.getReviewId(),
                reviewUpdateDTO.getRating(),
                reviewUpdateDTO.getReviewText(),
                review.getPostedBy(),
                review.getSeller(),
                review.getProduct(),
                null
        );
    }


}
