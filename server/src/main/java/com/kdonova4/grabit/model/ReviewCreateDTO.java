package com.kdonova4.grabit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateDTO {
    private int rating;
    private String reviewText;
    private int posterId;
    private int sellerId;
    private int productId;
}
