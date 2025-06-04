package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.data.ShoppingCartRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Image;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.ShoppingCart;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {

    @Mock
    ShoppingCartRepository shoppingCartRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    AppUserRepository appUserRepository;

    @InjectMocks
    ShoppingCartService service;

    private ShoppingCart shoppingCart;
    private Product product;
    private AppUser appUser;

    @BeforeEach
    void setup() {
        appUser = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, appUser);
        shoppingCart = new ShoppingCart(1, product, appUser, 1);
    }

    @Test
    void shouldFindAll() {
        when(shoppingCartRepository.findAll()).thenReturn(
                List.of(
                        shoppingCart
                )
        );

        List<ShoppingCart> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(shoppingCartRepository).findAll();
    }

    @Test
    void shouldFindByUser() {
        when(shoppingCartRepository.findByUser(appUser)).thenReturn(List.of(shoppingCart));

        List<ShoppingCart> actual = service.findByUser(appUser);

        assertEquals(1, actual.size());
        verify(shoppingCartRepository).findByUser(appUser);
    }

    @Test
    void shouldFindByUserAndProduct() {
        when(shoppingCartRepository.findByUserAndProduct(appUser, product)).thenReturn(Optional.of(shoppingCart));

        Optional<ShoppingCart> actual = service.findByUserAndProduct(appUser, product);

        assertTrue(actual.isPresent());
    }

    @Test
    void shouldFindById() {
        when(shoppingCartRepository.findById(shoppingCart.getShoppingCartId())).thenReturn(Optional.of(shoppingCart));
        Optional<ShoppingCart> actual = service.findById(shoppingCart.getShoppingCartId());
        assertTrue(actual.isPresent());
    }

    @Test
    void shouldCreateValid() {
        ShoppingCart mockOut = shoppingCart;
        mockOut.setShoppingCartId(1);
        shoppingCart.setShoppingCartId(0);

        when(shoppingCartRepository.save(shoppingCart)).thenReturn(mockOut);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(appUserRepository.findById(appUser.getAppUserId())).thenReturn(Optional.of(appUser));
        when(shoppingCartRepository.findByUserAndProduct(appUser, product)).thenReturn(Optional.empty());

        Result<ShoppingCart> actual = service.create(shoppingCart);
        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(appUserRepository.findById(appUser.getAppUserId())).thenReturn(Optional.of(appUser));
        when(shoppingCartRepository.findByUserAndProduct(appUser, product)).thenReturn(Optional.of(shoppingCart));

        Result<ShoppingCart> actual = service.create(shoppingCart);
        assertEquals(ResultType.INVALID, actual.getType());

        shoppingCart.setShoppingCartId(0);
        shoppingCart.setQuantity(0);
        actual = service.create(shoppingCart);
        assertEquals(ResultType.INVALID, actual.getType());

        shoppingCart.setQuantity(1);
        shoppingCart.setUser(null);
        actual = service.create(shoppingCart);
        assertEquals(ResultType.INVALID, actual.getType());

        shoppingCart.setUser(appUser);
        shoppingCart.setProduct(null);
        actual = service.create(shoppingCart);
        assertEquals(ResultType.INVALID, actual.getType());


        shoppingCart.setProduct(product);
        ShoppingCart temp = shoppingCart;
        shoppingCart = null;
        actual = service.create(shoppingCart);
        assertEquals(ResultType.INVALID, actual.getType());

        shoppingCart = temp;
        actual = service.create(shoppingCart);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldDeleteByUser() {
        doNothing().when(shoppingCartRepository).deleteByUser(appUser);
        service.deleteByUser(appUser);
        verify(shoppingCartRepository).deleteByUser(appUser);
    }

    @Test
    void shouldDeleteById() {
        when(shoppingCartRepository.findById(shoppingCart.getShoppingCartId())).thenReturn(Optional.of(shoppingCart));
        doNothing().when(shoppingCartRepository).deleteById(shoppingCart.getShoppingCartId());
        assertTrue(service.deleteById(shoppingCart.getShoppingCartId()));
        verify(shoppingCartRepository).deleteById(shoppingCart.getShoppingCartId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(shoppingCartRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(shoppingCartRepository, never()).deleteById(anyInt());
    }

}
