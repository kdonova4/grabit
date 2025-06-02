package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.OrderProduct;
import com.kdonova4.grabit.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    AppUserRepository appUserRepository;

    @InjectMocks
    ProductService service;

    private AppUser user;
    private Product product;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, user);
    }

    @Test
    void shouldFindAll() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(productRepository).findAll();
    }

    @Test
    void shouldFindByUser() {
        when(productRepository.findByUser(product.getUser())).thenReturn(List.of(product));

        List<Product> actual = service.findByUser(product.getUser());

        assertEquals(1, actual.size());
        verify(productRepository).findByUser(product.getUser());
    }

    @Test
    void shouldFindById() {
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        Optional<Product> actual = service.findById(product.getProductId());

        assertTrue(actual.isPresent());
        verify(productRepository).findById(product.getProductId());
    }
}
