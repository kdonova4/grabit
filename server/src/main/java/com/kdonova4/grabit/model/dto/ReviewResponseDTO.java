package com.kdonova4.grabit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private int reviewId;
    private int rating;
    private String reviewText;
    private int posterId;
    private int sellerId;
    private int productId;
    private Timestamp createdAt;
}
