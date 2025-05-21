package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.Image;
import com.kdonova4.grabit.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByProduct() {
        Optional<Product> product = productRepository.findById(1);
        List<Image> images = imageRepository.findByProduct(product.get());

        assertEquals("http://example.com/laptop.jpg", images.get(0).getImageUrl());
    }

    @Test
    void shouldCreate() {
        Optional<Product> product = productRepository.findById(1);
        Image image = new Image(0, "http://example.com/laptop2.jpg", product.get());
        imageRepository.save(image);

        assertEquals(4, imageRepository.findAll().size());
    }

    @Test
    void shouldUpdate() {
        Optional<Product> product = productRepository.findById(1);
        Image image = new Image(1, "http://example.com/laptop-new.jpg", product.get());
        imageRepository.save(image);

        List<Image> images = imageRepository.findByProduct(product.get());
        assertEquals("http://example.com/laptop-new.jpg", images.get(0).getImageUrl());
    }

    @Test
    void shouldDelete() {
        imageRepository.deleteById(1);

        assertEquals(2, imageRepository.findAll().size());
    }
}
