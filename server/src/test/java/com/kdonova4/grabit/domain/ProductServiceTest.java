package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.BidRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Bid;
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

    @Mock
    BidRepository bidRepository;

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

    @Test
    void shouldCreateValid() {
        Product mockOut = new Product(product);

        product.setProductId(0);

        when(productRepository.save(product)).thenReturn(mockOut);
        when(appUserRepository.findById(product.getUser().getAppUserId())).thenReturn(Optional.of(user));

        Result<Product> actual = service.create(product);

        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldUpdateValid() {
        product.setDescription("Updated description");

        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(appUserRepository.findById(product.getUser().getAppUserId())).thenReturn(Optional.of(user));
        when(bidRepository.findByProduct(product)).thenReturn(List.of());

        Result<Product> actual = service.update(product);
        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotCreateInvalid() {
        when(appUserRepository.findById(product.getUser().getAppUserId())).thenReturn(Optional.of(user));

        Result<Product> actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setProductId(0);
        product.setUser(null);
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setUser(user);
        product.setSaleType(SaleType.AUCTION);
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setSaleType(SaleType.BUY_NOW);
        product.setPrice(null);
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setPrice(new BigDecimal(-1200));
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setPrice(new BigDecimal(1200));
        product.setPostedAt(Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setPostedAt(Timestamp.valueOf(LocalDateTime.now().minusDays(3)));
        product.setProductName(null);
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setProductName("Test");
        product.setDescription(null);
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setDescription("Test description");
        product.setQuantity(0);
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setQuantity(5);
        product.setSaleType(SaleType.AUCTION);
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setSaleType(SaleType.BUY_NOW);
        product.setCondition(null);
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setCondition(ConditionType.EXCELLENT);
        product = null;
        actual = service.create(product);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotUpdateMissingOrInvalid() {

        product.setDescription("Updated description");

        when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());
        when(appUserRepository.findById(product.getUser().getAppUserId())).thenReturn(Optional.of(user));
        when(bidRepository.findByProduct(product)).thenReturn(List.of());

        Result<Product> actual = service.update(product);
        assertEquals(ResultType.INVALID, actual.getType());

        Bid bid = new Bid(1, new BigDecimal(255), Timestamp.valueOf(LocalDateTime.now()), product, user);
        product.setDescription("Updated description");

        actual = service.update(product);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setProductStatus(ProductStatus.SOLD);

        bid.setProduct(null);

        actual = service.update(product);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldDeleteById() {
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(product.getProductId());
        assertTrue(service.deleteById(product.getProductId()));
        verify(productRepository).deleteById(product.getProductId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(productRepository, never()).deleteById(anyInt());
    }


    @Test
    void shouldNotDeleteByIdWhereProductIsSold() {
        product.setProductStatus(ProductStatus.SOLD);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        assertFalse(service.deleteById(product.getProductId()));
        verify(productRepository, never()).deleteById(anyInt());
    }


}
