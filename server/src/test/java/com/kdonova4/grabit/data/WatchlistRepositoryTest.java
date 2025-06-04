package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.ShoppingCart;
import com.kdonova4.grabit.model.Watchlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class WatchlistRepositoryTest {

    @Autowired
    WatchlistRepository repository;

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
        Optional<AppUser> appUser = appUserRepository.findById(2);

        List<Watchlist> items = repository.findByUser(appUser.get());

        assertEquals(1, items.size());
    }

    @Test
    void shouldFindByUserAndProduct() {
        Optional<AppUser> appUser = appUserRepository.findById(2);
        Optional<Product> product = productRepository.findById(3);

        Optional<Watchlist> item = repository.findByUserAndProduct(appUser.get(), product.get());

        assertNotNull(item.get());
    }

    @Test
    void shouldCreate() {
        Optional<AppUser> appUser = appUserRepository.findById(1);
        Optional<Product> product = productRepository.findById(1);

        Watchlist item = new Watchlist(0 , product.get(), appUser.get());
        repository.save(item);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);

        assertEquals(0, repository.findAll().size());
    }
}
