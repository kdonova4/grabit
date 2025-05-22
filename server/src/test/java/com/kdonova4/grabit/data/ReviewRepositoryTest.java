package com.kdonova4.grabit.data;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class ReviewRepositoryTest {

    @Autowired
    ReviewRepository repository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByPostedBy() {
        Optional<AppUser> appUser = appUserRepository.findById(1);

        List<Review> reviews = repository.findByPostedBy(appUser.get());

        assertEquals(1, reviews.size());
    }

    @Test
    void shouldFindBySeller() {
        Optional<AppUser> appUser = appUserRepository.findById(2);

        List<Review> reviews = repository.findBySeller(appUser.get());

        assertEquals(1, reviews.size());
    }

    @Test
    void shouldFindByProduct() {
        Optional<Product> product = productRepository.findById(2);

        List<Review> reviews = repository.findByProduct(product.get());

        assertEquals(1, reviews.size());
    }

    @Test
    void shouldCreate() {
        Optional<AppUser> appUser = appUserRepository.findById(4);
        Optional<AppUser> seller = appUserRepository.findById(2);
        Optional<Product> product = productRepository.findById(3);

        Review review = new Review(0, 3, "Took forever to respond to my offer", appUser.get(), seller.get(), product.get(), Timestamp.valueOf(LocalDateTime.now()));
        repository.save(review);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldUpdate() {
        Optional<AppUser> appUser = appUserRepository.findById(1);
        Optional<AppUser> seller = appUserRepository.findById(2);
        Optional<Product> product = productRepository.findById(2);
        Review review = new Review(1, 4, "Great seller, cant wait!", appUser.get(), seller.get(), product.get(), Timestamp.valueOf(LocalDateTime.now()));
        repository.save(review);

        assertEquals(4, repository.findById(1).get().getRating());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);

        assertEquals(0, repository.findAll().size());
    }
}
