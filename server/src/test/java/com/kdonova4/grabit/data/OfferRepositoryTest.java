package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Offer;
import com.kdonova4.grabit.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class OfferRepositoryTest {

    @Autowired
    OfferRepository repository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByUser() {
        Optional<AppUser> user = appUserRepository.findById(4);

        List<Offer> offers = repository.findByUser(user.get());

        assertEquals(1, offers.size());
    }

    @Test
    void shouldFindByProduct() {
        Optional<Product> product = productRepository.findById(1);

        List<Offer> offers = repository.findByProduct(product.get());

        assertEquals(1, offers.size());
    }

    @Test
    void shouldFindByUserAndProduct() {
        Optional<AppUser> user = appUserRepository.findById(4);
        Optional<Product> product = productRepository.findById(1);

        Optional<Offer> offer = repository.findByUserAndProduct(user.get(), product.get());

        assertNotNull(offer.get());
    }

    @Test
    void shouldFindByProductAndExpireDateAfter() {
        Optional<Product> product = productRepository.findById(1);

        List<Offer> offers = repository.findByProductAndExpireDateAfter(product.get(), LocalDateTime.now());

        assertEquals(1, offers.size());
    }

    @Test
    void shouldCreate() {
        Optional<AppUser> user = appUserRepository.findById(1);
        Optional<Product> product = productRepository.findById(1);

        Offer offer = new Offer(0, new BigDecimal(1000), Timestamp.valueOf(LocalDateTime.now()), "Can you do 1000?", user.get(), product.get(), LocalDateTime.now().plusDays(1));

        repository.save(offer);

        assertEquals("Can you do 1000?", repository.findById(2).get().getMessage());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);

        assertEquals(0, repository.findAll().size());
    }
}
