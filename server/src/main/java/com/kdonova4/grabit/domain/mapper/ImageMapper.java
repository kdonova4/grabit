package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.entity.Image;
import com.kdonova4.grabit.model.dto.ImageCreateDTO;
import com.kdonova4.grabit.model.dto.ImageResponseDTO;
import com.kdonova4.grabit.model.entity.Product;

public class ImageMapper {

    public static ImageResponseDTO toResponseDTO(Image image) {
        return new ImageResponseDTO(
                image.getImageId(),
                image.getImageUrl(),
                image.getProduct().getProductId()
        );
    }

    public static Image toImage(ImageCreateDTO imageCreateDTO, Product product) {
        return new Image(
                0,
                imageCreateDTO.getImageUrl(),
                product
        );
    }
}
