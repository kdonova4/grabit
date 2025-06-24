package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.BidRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.domain.mapper.ProductMapper;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.*;
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
        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
                product.getSaleType(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getUser().getAppUserId()
        );

        Product mockOut = new Product(product);
        product.setProductId(0);

        when(productRepository.save(any(Product.class))).thenReturn(mockOut);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        Result<Object> actual = service.create(productCreateDTO);

        assertEquals(ResultType.SUCCESS, actual.getType());
        assertTrue(actual.getPayload() instanceof ProductBuyNowResponseDTO);
    }

    @Test
    void shouldUpdateValid() {
        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO(
                product.getProductId(),
                "Test",
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getProductStatus(),
                product.getWinningBid()
        );

        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(appUserRepository.findById(product.getUser().getAppUserId())).thenReturn(Optional.of(user));
        when(bidRepository.findByProduct(any(Product.class))).thenReturn(List.of());


        Result<Object> actual = service.update(productUpdateDTO);

        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotCreateInvalid() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));




        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
                product.getSaleType(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCondition(),
                product.getQuantity(),
                product.getUser().getAppUserId()
        );


        productCreateDTO.setPrice(null);
        Result<Object> actual = service.create(productCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        productCreateDTO.setPrice(new BigDecimal(1200));
        productCreateDTO.setProductName(null);
        actual = service.create(productCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        productCreateDTO.setProductName(product.getDescription());
        productCreateDTO.setDescription(null);
        actual = service.create(productCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        productCreateDTO.setDescription(product.getDescription());
        productCreateDTO.setQuantity(-5);
        actual = service.create(productCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());

        productCreateDTO.setQuantity(1);
        productCreateDTO.setConditionType(null);
        actual = service.create(productCreateDTO);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotUpdateMissingOrInvalid() {
        product.setDescription("Updated description");

        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(appUserRepository.findById(product.getUser().getAppUserId())).thenReturn(Optional.of(user));
        when(bidRepository.findByProduct(product)).thenReturn(List.of());


        Result<Object> actual = service.update(ProductMapper.toUpdateDTO(product));
        assertEquals(ResultType.NOT_FOUND, actual.getType());

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        Bid bid = new Bid(1, new BigDecimal(255), Timestamp.valueOf(LocalDateTime.now()), product, user);
        product.setDescription("Updated description");
        product.setSaleType(SaleType.AUCTION);
        when(bidRepository.findByProduct(product)).thenReturn(List.of(bid));
        actual = service.update(ProductMapper.toUpdateDTO(product));
        assertEquals(ResultType.INVALID, actual.getType());

        product.setProductStatus(ProductStatus.SOLD);

        bid.setProduct(null);

        actual = service.update(ProductMapper.toUpdateDTO(product));
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
