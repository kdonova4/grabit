package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class ShoppingCartRepositoryTest {

    @Autowired
    ShoppingCartRepository repository;

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
        Optional<AppUser> appUser = appUserRepository.findById(1);

        List<ShoppingCart> items = repository.findByUser(appUser.get());

        assertEquals(1, items.size());
    }

    @Test
    void shouldFindByUserAndProduct() {
        Optional<AppUser> appUser = appUserRepository.findById(1);
        Optional<Product> product = productRepository.findById(2);

        Optional<ShoppingCart> item = repository.findByUserAndProduct(appUser.get(), product.get());

        assertNotNull(item.get());
    }

    @Test
    void shouldCreate() {
        Optional<AppUser> appUser = appUserRepository.findById(1);
        Optional<Product> product = productRepository.findById(1);

        ShoppingCart item = new ShoppingCart(0 , product.get(), appUser.get(), 1);
        repository.save(item);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldUpdate() {
        Optional<AppUser> appUser = appUserRepository.findById(1);
        Optional<Product> product = productRepository.findById(2);

        ShoppingCart item = new ShoppingCart(1, product.get(), appUser.get(), 5);
        repository.save(item);
        assertEquals(5, repository.findAll().get(0).getQuantity());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);

        assertEquals(0, repository.findAll().size());
    }

}
