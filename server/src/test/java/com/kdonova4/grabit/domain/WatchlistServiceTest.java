package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.data.WatchlistRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.ShoppingCart;
import com.kdonova4.grabit.model.Watchlist;
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
public class WatchlistServiceTest {

    @Mock
    WatchlistRepository watchlistRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    AppUserRepository appUserRepository;

    @InjectMocks
    WatchlistService service;

    private Watchlist item;
    private Product product;
    private AppUser appUser;

    @BeforeEach
    void setup() {
        appUser = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, appUser);
        item = new Watchlist(1 , product, appUser);
    }

    @Test
    void shouldFindAll() {
        when(watchlistRepository.findAll()).thenReturn(
                List.of(
                        item
                )
        );

        List<Watchlist> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(watchlistRepository).findAll();
    }

    @Test
    void shouldFindByUser() {
        when(watchlistRepository.findByUser(appUser)).thenReturn(List.of(item));

        List<Watchlist> actual = service.findByUser(appUser);

        assertEquals(1, actual.size());
        verify(watchlistRepository).findByUser(appUser);
    }

    @Test
    void shouldFindByUserAndProduct() {
        when(watchlistRepository.findByUserAndProduct(appUser, product)).thenReturn(Optional.of(item));

        Optional<Watchlist> actual = service.findByUserAndProduct(appUser, product);

        assertTrue(actual.isPresent());
    }

    @Test
    void shouldFindById() {
        when(watchlistRepository.findById(item.getWatchId())).thenReturn(Optional.of(item));
        Optional<Watchlist> actual = service.findById(item.getWatchId());
        assertTrue(actual.isPresent());
    }

    @Test
    void shouldCreateValid() {
        Watchlist mockOut = item;
        mockOut.setWatchId(1);
        item.setWatchId(0);

        when(watchlistRepository.save(item)).thenReturn(mockOut);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(appUserRepository.findById(appUser.getAppUserId())).thenReturn(Optional.of(appUser));
        when(watchlistRepository.findByUserAndProduct(appUser, product)).thenReturn(Optional.empty());

        Result<Watchlist> actual = service.create(item);
        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(appUserRepository.findById(appUser.getAppUserId())).thenReturn(Optional.of(appUser));
        when(watchlistRepository.findByUserAndProduct(appUser, product)).thenReturn(Optional.of(item));

        Result<Watchlist> actual = service.create(item);
        assertEquals(ResultType.INVALID, actual.getType());

        item.setWatchId(0);
        item.setUser(null);
        actual = service.create(item);
        assertEquals(ResultType.INVALID, actual.getType());

        item.setUser(appUser);
        item.setProduct(null);
        actual = service.create(item);
        assertEquals(ResultType.INVALID, actual.getType());


        item.setProduct(product);
        Watchlist temp = item;
        item = null;
        actual = service.create(item);
        assertEquals(ResultType.INVALID, actual.getType());

        item = temp;
        actual = service.create(item);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldDeleteById() {
        when(watchlistRepository.findById(item.getWatchId())).thenReturn(Optional.of(item));
        doNothing().when(watchlistRepository).deleteById(item.getWatchId());
        assertTrue(service.deleteById(item.getWatchId()));
        verify(watchlistRepository).deleteById(item.getWatchId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(watchlistRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(watchlistRepository, never()).deleteById(anyInt());
    }


}
