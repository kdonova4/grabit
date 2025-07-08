package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.ImageRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.entity.Image;
import com.kdonova4.grabit.model.dto.ImageCreateDTO;
import com.kdonova4.grabit.model.dto.ImageResponseDTO;
import com.kdonova4.grabit.model.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    ImageRepository imageRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ImageService service;

    private Image image;
    private Product product;

    @BeforeEach
    void setup() {
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, null, null);
        image = new Image(1, "http://example.com/laptop2.jpg", product);
    }

    @Test
    void shouldFindAll() {
        when(imageRepository.findAll()).thenReturn(
                List.of(
                        image
                )
        );

        List<Image> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(imageRepository).findAll();
    }

    @Test
    void shouldFindByProduct() {
        when(imageRepository.findByProduct(product)).thenReturn(
                List.of(
                        image
                )
        );

        List<Image> actual = service.findByProduct(product);

        assertEquals(1, actual.size());
        verify(imageRepository).findByProduct(product);
    }

    @Test
    void shouldFindById() {
        when(imageRepository.findById(image.getImageId())).thenReturn(Optional.of(image));
        Optional<Image> actual = service.findById(image.getImageId());
        assertTrue(actual.isPresent());
    }

//    @Test
//    void shouldCreateValid() {
//        Image mockOut = image;
//        mockOut.setImageId(1);
//        image.setImageId(0);
//
//        when(productRepository.findById(image.getProduct().getProductId())).thenReturn(Optional.of(product));
//        when(imageRepository.save(image)).thenReturn(mockOut);
//
//        ImageCreateDTO imageCreateDTO = new ImageCreateDTO(
//                image.getImageUrl(),
//                image.getProduct().getProductId()
//        );
//
//        Result<ImageResponseDTO> actual = service.create(imageCreateDTO);
//
//        assertEquals(ResultType.SUCCESS, actual.getType());
//    }
//
//    @Test
//    void shouldNotCreateInvalid() {
//
//        ImageCreateDTO imageCreateDTO = new ImageCreateDTO(
//                image.getImageUrl(),
//                image.getProduct().getProductId()
//        );
//        when(productRepository.findById(image.getProduct().getProductId())).thenReturn(Optional.empty());
//
//        Result<ImageResponseDTO> actual = service.create(imageCreateDTO);
//        assertEquals(ResultType.INVALID, actual.getType());
//
//        imageCreateDTO.setImageUrl(null);
//        actual = service.create(imageCreateDTO);
//        assertEquals(ResultType.INVALID, actual.getType());
//    }

    @Test
    void shouldDeleteById() {
        when(imageRepository.findById(image.getImageId())).thenReturn(Optional.of(image));
        doNothing().when(imageRepository).deleteById(image.getImageId());
        assertTrue(service.deleteById(image.getImageId()));
        verify(imageRepository).deleteById(image.getImageId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(imageRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(imageRepository, never()).deleteById(anyInt());
    }
}
