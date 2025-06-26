package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.BidRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.dto.BidCreateDTO;
import com.kdonova4.grabit.model.dto.BidResponseDTO;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Bid;
import com.kdonova4.grabit.model.entity.Product;
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
public class BidServiceTest {

    @Mock
    BidRepository bidRepository;

    @Mock
    AppUserRepository appUserRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    BidService service;

    private AppUser user;
    private Product product;
    private Bid bid;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.AUCTION, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(250), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, LocalDateTime.now().plusDays(1), null, null, user);
        bid = new Bid(1, new BigDecimal(255), Timestamp.valueOf(LocalDateTime.now()), product, user);
    }

    @Test
    void shouldFindAll() {
        when(bidRepository.findAll()).thenReturn(List.of(bid));

        List<Bid> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(bidRepository).findAll();
    }

    @Test
    void shouldFindByUser() {
        when(bidRepository.findByUser(user)).thenReturn(List.of(bid));

        List<Bid> actual = service.findByUser(user);

        assertEquals(1, actual.size());
        verify(bidRepository).findByUser(user);
    }

    @Test
    void shouldFindByProduct() {
        when(bidRepository.findByProduct(product)).thenReturn(List.of(bid));

        List<Bid> actual = service.findByProduct(product);

        assertEquals(1, actual.size());
        verify(bidRepository).findByProduct(product);
    }

    @Test
    void shouldFindById() {
        when(bidRepository.findById(bid.getBidId())).thenReturn(Optional.of(bid));

        Optional<Bid> actual = service.findById(bid.getBidId());

        assertTrue(actual.isPresent());
        verify(bidRepository).findById(bid.getBidId());
    }

    @Test
    void shouldCreateValid() {
        Bid mockOut = bid;
        bid.setBidId(0);
        bid.setPlacedAt(null);
        when(bidRepository.save(bid)).thenReturn(mockOut);
        when(appUserRepository.findById(bid.getUser().getAppUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(bid.getProduct().getProductId())).thenReturn(Optional.of(product));

        BidCreateDTO bidCreateDTO = new BidCreateDTO(
                bid.getUser().getAppUserId(),
                bid.getProduct().getProductId(),
                bid.getBidAmount()
        );

        Result<BidResponseDTO> actual = service.create(bidCreateDTO);
        System.out.println(actual.getMessages());
        assertEquals(ResultType.SUCCESS, actual.getType());

    }

    @Test
    void shouldNotCreateInvalid() {
        when(appUserRepository.findById(bid.getUser().getAppUserId())).thenReturn(Optional.empty());
        when(productRepository.findById(bid.getProduct().getProductId())).thenReturn(Optional.empty());

        BidCreateDTO bidCreateDTO = new BidCreateDTO(
                bid.getUser().getAppUserId(),
                bid.getProduct().getProductId(),
                bid.getBidAmount()
        );

        Result<BidResponseDTO> actual = service.create(bidCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        bidCreateDTO.setBidAmount(new BigDecimal(-1));
        actual = service.create(bidCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        when(productRepository.findById(bid.getProduct().getProductId())).thenReturn(Optional.of(product));

        product.setSaleType(SaleType.BUY_NOW);
        bidCreateDTO.setBidAmount(new BigDecimal(300));
        actual = service.create(bidCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        bidCreateDTO.setBidAmount(new BigDecimal(5));
        actual = service.create(bidCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldDeleteById() {
        when(bidRepository.findById(bid.getBidId())).thenReturn(Optional.of(bid));
        doNothing().when(bidRepository).deleteById(bid.getBidId());
        assertTrue(service.deleteById(bid.getBidId()));
        verify(bidRepository).deleteById(bid.getBidId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(bidRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(bidRepository, never()).deleteById(anyInt());
    }
}
