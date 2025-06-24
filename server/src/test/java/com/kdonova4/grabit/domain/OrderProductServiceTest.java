package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.OrderProductRepository;
import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.model.OrderProduct;
import com.kdonova4.grabit.model.OrderProductResponseDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderProductServiceTest {

    @Mock
    OrderProductRepository orderProductRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    OrderProductService service;

    private Order order;
    private Product product;
    private OrderProduct orderProduct;

    @BeforeEach
    void setup() {
        order = new Order(1, null, Timestamp.valueOf(LocalDateTime.now()), null, null, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, null);
        orderProduct = new OrderProduct(1, order, product, 1, new BigDecimal(1200), new BigDecimal(1200));
    }

    @Test
    void shouldFindAll() {
        when(orderProductRepository.findAll()).thenReturn(List.of(orderProduct));

        List<OrderProduct> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(orderProductRepository).findAll();
    }

    @Test
    void shouldFindByProduct() {
        when(orderProductRepository.findByProduct(orderProduct.getProduct())).thenReturn(List.of(orderProduct));

        List<OrderProduct> actual = service.findByProduct(orderProduct.getProduct());

        assertEquals(1, actual.size());
        verify(orderProductRepository).findByProduct(orderProduct.getProduct());
    }

    @Test
    void shouldFindByOrder() {
        when(orderProductRepository.findByOrder(orderProduct.getOrder())).thenReturn(List.of(orderProduct));

        List<OrderProduct> actual = service.findByOrder(orderProduct.getOrder());

        assertEquals(1, actual.size());
        verify(orderProductRepository).findByOrder(orderProduct.getOrder());
    }

    @Test
    void shouldFindById() {
        when(orderProductRepository.findById(orderProduct.getOrderProductId())).thenReturn(Optional.of(orderProduct));

        Optional<OrderProduct> actual = service.findById(orderProduct.getOrderProductId());

        assertTrue(actual.isPresent());
        verify(orderProductRepository).findById(orderProduct.getOrderProductId());
    }

    @Test
    void shouldCreateValid() {
        OrderProduct mockOut = orderProduct;
        orderProduct.setOrderProductId(0);

        when(orderProductRepository.save(orderProduct)).thenReturn(mockOut);
        when(productRepository.findById(orderProduct.getProduct().getProductId())).thenReturn(Optional.of(product));
        when(orderRepository.findById(orderProduct.getOrder().getOrderId())).thenReturn(Optional.of(order));

        Result<OrderProductResponseDTO> actual = service.create(orderProduct);

        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotCreateInvalid() {

        when(productRepository.findById(orderProduct.getProduct().getProductId())).thenReturn(Optional.of(product));
        when(orderRepository.findById(orderProduct.getOrder().getOrderId())).thenReturn(Optional.of(order));

        Result<OrderProductResponseDTO> actual = service.create(orderProduct);
        assertEquals(ResultType.INVALID, actual.getType());

        orderProduct.setOrderProductId(0);
        orderProduct.setProduct(null);
        actual = service.create(orderProduct);
        assertEquals(ResultType.INVALID, actual.getType());

        orderProduct.setProduct(product);
        orderProduct.setOrder(null);
        actual = service.create(orderProduct);
        assertEquals(ResultType.INVALID, actual.getType());

        orderProduct.setOrder(order);
        orderProduct.setQuantity(0);
        actual = service.create(orderProduct);
        assertEquals(ResultType.INVALID, actual.getType());

        orderProduct.setQuantity(1);
        product.setWinningBid(new BigDecimal(1600));
        order.setTotalAmount(new BigDecimal(1600));
        product.setSaleType(SaleType.AUCTION);
        actual = service.create(orderProduct);
        assertEquals(ResultType.INVALID, actual.getType());

        orderProduct.setUnitPrice(new BigDecimal(500));
        product.setSaleType(SaleType.BUY_NOW);
        actual = service.create(orderProduct);
        assertEquals(ResultType.INVALID, actual.getType());

        orderProduct.setUnitPrice(new BigDecimal(1600));
        orderProduct = null;
        actual = service.create(orderProduct);
        assertEquals(ResultType.INVALID, actual.getType());
    }
}
