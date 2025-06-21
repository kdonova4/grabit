package com.kdonova4.grabit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDTO {

    private int imageId;
    private String imageUrl;
    private int productId;
}
