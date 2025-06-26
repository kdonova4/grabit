package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.data.ShoppingCartRepository;
import com.kdonova4.grabit.domain.mapper.ShoppingCartMapper;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.dto.ShoppingCartDTO;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ShoppingCart;
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
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, null,appUser);
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
        ShoppingCart mockOut = new ShoppingCart(shoppingCart.getShoppingCartId(), product, appUser, 1);
        shoppingCart.setShoppingCartId(0);


        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(mockOut);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(appUserRepository.findById(1)).thenReturn(Optional.of(appUser));

        ShoppingCartDTO shoppingCartDTO = ShoppingCartMapper.toResponseDTO(shoppingCart);

        Result<ShoppingCartDTO> actual = service.create(shoppingCartDTO);

        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotCreateInvalid() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        ShoppingCartDTO shoppingCartDTO = ShoppingCartMapper.toResponseDTO(shoppingCart);

        Result<ShoppingCartDTO> actual = service.create(shoppingCartDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        shoppingCartDTO.setShoppingCartId(0);
        actual = service.create(shoppingCartDTO);
        assertEquals(ResultType.INVALID, actual.getType());


        when(appUserRepository.findById(1)).thenReturn(Optional.of(appUser));
        actual = service.create(shoppingCartDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        when(appUserRepository.findById(1)).thenReturn(Optional.empty());
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        actual = service.create(shoppingCartDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        when(appUserRepository.findById(1)).thenReturn(Optional.of(appUser));
        shoppingCartDTO.setQuantity(-5);
        actual = service.create(shoppingCartDTO);
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
