package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Bid;
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
public class BidRepositoryTest {

    @Autowired
    BidRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByUser() {
        Optional<AppUser> appUser = appUserRepository.findById(1);

        List<Bid> bids = repository.findByUser(appUser.get());

        assertEquals(1, bids.size());
    }

    @Test
    void shouldFindByProduct() {
        Optional<Product> product = productRepository.findById(2);

        List<Bid> bids = repository.findByProduct(product.get());

        assertEquals(2, bids.size());
    }

    @Test
    void shouldCreate() {
        Optional<AppUser> appUser = appUserRepository.findById(2);
        Optional<Product> product = productRepository.findById(2);

        Bid bid = new Bid(0, new BigDecimal(45.00), Timestamp.valueOf(LocalDateTime.now()),product.get(), appUser.get());

        repository.save(bid);

        assertEquals(3, repository.findAll().size());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(2);

        assertEquals(1, repository.findAll().size());
    }
}
