package com.kdonova4.grabit.data;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
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
public class ProductRepositoryTest {

    @Autowired
    ProductRepository repository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByUser() {
        Optional<AppUser> appUser = appUserRepository.findById(2);

        List<Product> products = repository.findByUser(appUser.get());

        assertEquals(3, products.size());
    }

    @Test
    void shouldFindProductsByName() {
        List<Product> products = repository.search("Book Set", null, null, null, null, null, null);

        assertEquals(1, products.size());
    }

    @Test
    void shouldFindProductsByNameAndMinPrice() {
        List<Product> products = repository.search("Book Set", new BigDecimal(29), null, null, null, null, null);

        assertEquals(1, products.size());
    }

    @Test
    void shouldFindProductsByNameAndBetweenPrices() {
        List<Product> products = repository.search("top", new BigDecimal(20), new BigDecimal(1600), null, null, null, null);

        assertEquals(1, products.size());
    }

    @Test
    void shouldFindProductsByNameAndBetweenPricesAndStatus() {
        List<Product> products = repository.search("laptop", new BigDecimal(1000), new BigDecimal(1600), ProductStatus.ACTIVE, null, null, null);

        assertEquals(1, products.size());

        products = repository.search("", new BigDecimal(1000), new BigDecimal(1600), ProductStatus.SOLD, null, null, null);

        assertEquals(1, products.size());

        products = repository.search("", new BigDecimal(25), new BigDecimal(160), ProductStatus.ACTIVE, null, null, null);

        assertEquals(1, products.size());
    }

    @Test
    void shouldFindProductsByNameAndBetweenPricesAndStatusAndCondition() {
        List<Product> products = repository.search("", new BigDecimal(1000), new BigDecimal(1600), null, ConditionType.EXCELLENT, null, null);

        assertEquals(2, products.size());

        products = repository.search("", new BigDecimal(1000), new BigDecimal(1600), ProductStatus.ACTIVE, ConditionType.EXCELLENT, null, null);

        assertEquals(1, products.size());

        products = repository.search("", new BigDecimal(0), new BigDecimal(1600), ProductStatus.ACTIVE, ConditionType.GOOD, null, null);

        assertEquals(1, products.size());
    }

    @Test
    void shouldFindProductsByNameAndBetweenPricesAndStatusAndConditionAndSaleType() {
        List<Product> products = repository.search("", null, null, null, null, SaleType.BUY_NOW, null);

        assertEquals(2, products.size());

        products = repository.search("", null, null, null, null, SaleType.AUCTION, null);

        assertEquals(1, products.size());
    }

    @Test
    void shouldFindProductsByNameAndBetweenPricesAndStatusAndConditionAndSaleTypeAndCategory() {
        Optional<Category> category = categoryRepository.findById(1);

        List<Product> products = repository.search("", null, null, null, null, null, category.get());

        assertEquals(2, products.size());

        products = repository.search("", null, null, ProductStatus.ACTIVE, ConditionType.EXCELLENT, null, category.get());

        assertEquals(1, products.size());
    }

    @Test
    void shouldCreate() {
        Optional<AppUser> appUser = appUserRepository.findById(2);
        Optional<Category> category = categoryRepository.findById(1);

        Product product = new Product(0, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, null, appUser.get());

        repository.save(product);

        assertEquals(4, repository.findAll().size());
    }

    @Test
    void shouldUpdate() {
        Optional<AppUser> appUser = appUserRepository.findById(2);

        Product product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, null, appUser.get());
        repository.save(product);
        assertEquals("Electric Guitar", repository.findById(1).get().getProductName());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);
        assertEquals(2, repository.findAll().size());
    }

}
