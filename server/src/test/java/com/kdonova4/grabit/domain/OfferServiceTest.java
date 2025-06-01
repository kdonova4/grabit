package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.OfferRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Bid;
import com.kdonova4.grabit.model.Offer;
import com.kdonova4.grabit.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OfferServiceTest {

    @Mock
    OfferRepository offerRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    AppUserRepository appUserRepository;

    @InjectMocks
    OfferService service;

    private AppUser user;
    private Product product;
    private Offer offer;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.AUCTION, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, LocalDateTime.now().plusDays(1), null, user);
        offer = new Offer(1, new BigDecimal(1000), Timestamp.valueOf(LocalDateTime.now()), "Can you do 1000?", user, product, LocalDateTime.now().plusDays(1));
    }

    @Test
    void shouldFindAll() {
        when(offerRepository.findAll()).thenReturn(List.of(offer));

        List<Offer> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(offerRepository).findAll();
    }

    @Test
    void shouldFindByUser() {
        when(offerRepository.findByUser(user)).thenReturn(List.of(offer));

        List<Offer> actual = service.findByUser(user);

        assertEquals(1, actual.size());
        verify(offerRepository).findByUser(user);
    }

    @Test
    void shouldFindByProduct() {
        when(offerRepository.findByProduct(product)).thenReturn(List.of(offer));

        List<Offer> actual = service.findByProduct(product);

        assertEquals(1, actual.size());
        verify(offerRepository).findByProduct(product);
    }

    @Test
    void shouldFindByUserAndProduct() {
        when(offerRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(offer));

        Optional<Offer> actual = service.findByUserAndProduct(user, product);

        assertTrue(actual.isPresent());
        verify(offerRepository).findByUserAndProduct(user, product);
    }

    @Test
    void shouldFindByProductAndExpireDateAfter() {
        LocalDateTime time = LocalDateTime.now();
        when(offerRepository.findByProductAndExpireDateAfter(product, time)).thenReturn(List.of(offer));

        List<Offer> actual = service.findByProductAndExpireDateAfter(product, time);

        assertEquals(1, actual.size());
        verify(offerRepository).findByProductAndExpireDateAfter(product, time);
    }

    @Test
    void shouldFindById() {
        when(offerRepository.findById(offer.getOfferId())).thenReturn(Optional.of(offer));

        Optional<Offer> actual = service.findById(offer.getOfferId());

        assertTrue(actual.isPresent());
        verify(offerRepository).findById(offer.getOfferId());
    }

    @Test
    void shouldCreateValid() {
        Offer mockOut = offer;
        offer.setOfferId(0);
        product.setSaleType(SaleType.BUY_NOW);
        product.setAuctionEnd(null);

        when(offerRepository.save(offer)).thenReturn(mockOut);
        when(productRepository.findById(offer.getProduct().getProductId())).thenReturn(Optional.of(product));
        when(appUserRepository.findById(offer.getUser().getAppUserId())).thenReturn(Optional.of(user));

        Result<Offer> actual = service.create(offer);

        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {

        when(productRepository.findById(offer.getProduct().getProductId())).thenReturn(Optional.of(product));
        when(appUserRepository.findById(offer.getUser().getAppUserId())).thenReturn(Optional.of(user));

        Result<Offer> actual = service.create(offer);
        assertEquals(ResultType.INVALID, actual.getType());

        offer.setOfferId(0);
        actual = service.create(offer);
        assertEquals(ResultType.INVALID, actual.getType());

        product.setSaleType(SaleType.BUY_NOW);
        offer.setSentAt(null);
        actual = service.create(offer);
        assertEquals(ResultType.INVALID, actual.getType());

        offer.setSentAt(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        offer.setOfferAmount(product.getPrice());
        actual = service.create(offer);
        assertEquals(ResultType.INVALID, actual.getType());

        offer.setOfferAmount(new BigDecimal(50));
        offer = null;
        actual = service.create(offer);
        assertEquals(ResultType.INVALID, actual.getType());
    }


    @Test
    void shouldDeleteById() {
        when(offerRepository.findById(offer.getOfferId())).thenReturn(Optional.of(offer));
        doNothing().when(offerRepository).deleteById(offer.getOfferId());
        assertTrue(service.deleteById(offer.getOfferId()));
        verify(offerRepository).deleteById(offer.getOfferId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(offerRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(offerRepository, never()).deleteById(anyInt());
    }
}
