package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.OrderProductRepository;
import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.enums.*;
import com.kdonova4.grabit.model.entity.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    AddressRepository addressRepository;

    @Mock
    AppUserRepository appUserRepository;

    @Mock
    OrderProductRepository orderProductRepository;

    @Mock
    OrderProductService orderProductService;

    @Mock
    ShipmentService shipmentService;

    @Mock
    PaymentService paymentService;

    @Mock
    ShoppingCartService shoppingCartService;

    @Mock
    ProductService productService;

    @InjectMocks
    OrderService service;

    private Order order;
    private Address address;
    private AppUser user;
    private Product product;
    private OrderProduct orderProduct;
    private Payment payment;
    private Shipment shipment;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        address = new Address(1, "345 Apple St", "Waxhaw", "NC", "28173", "USA", user);
        order = new Order(1, user, Timestamp.valueOf(LocalDateTime.now()), address, address, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());
        payment = new Payment(1, order, new BigDecimal(1200), Timestamp.valueOf(LocalDateTime.now()));
        shipment = new Shipment(1, order, ShipmentStatus.PENDING, "TRACKTRACKTRACK123", Timestamp.valueOf(LocalDateTime.now()), null);
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, null, user);
        orderProduct = new OrderProduct(1, order, product, 1, new BigDecimal(1200), new BigDecimal(1200));
        shoppingCart = new ShoppingCart(1, product, user, 1);
    }

    @Test
    void shouldFindAll() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(orderRepository).findAll();
    }

    @Test
    void shouldFindByUser() {
        when(orderRepository.findByUser(order.getUser())).thenReturn(List.of(order));

        List<Order> actual = service.findByUser(order.getUser());

        assertEquals(1, actual.size());
        verify(orderRepository).findByUser(order.getUser());
    }

    @Test
    void shouldFindByOrderStatus() {
        when(orderRepository.findByOrderStatus(order.getOrderStatus())).thenReturn(List.of(order));

        List<Order> actual = service.findByOrderStatus(order.getOrderStatus());

        assertEquals(1, actual.size());
        verify(orderRepository).findByOrderStatus(order.getOrderStatus());
    }

    @Test
    void shouldFindByOrderedAtAfter() {
        Timestamp time = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
        when(orderRepository.findByOrderedAtAfter(time)).thenReturn(List.of(order));

        List<Order> actual = service.findByOrderedAtAfter(time);

        assertEquals(1, actual.size());
        verify(orderRepository).findByOrderedAtAfter(time);
    }

    @Test
    void shouldFindByOrderedAtBetween() {
        Timestamp start = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
        Timestamp end = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
        when(orderRepository.findByOrderedAtBetween(start, end)).thenReturn(List.of(order));

        List<Order> actual = service.findByOrderedAtBetween(start, end);

        assertEquals(1, actual.size());
        verify(orderRepository).findByOrderedAtBetween(start, end);
    }

    @Test
    void shouldFindByShippingAddress() {
        when(orderRepository.findByShippingAddress(order.getShippingAddress())).thenReturn(List.of(order));

        List<Order> actual = service.findByShippingAddress(order.getShippingAddress());

        assertEquals(1, actual.size());
        verify(orderRepository).findByShippingAddress(order.getShippingAddress());
    }

    @Test
    void shouldFindByBillingAddress() {
        when(orderRepository.findByBillingAddress(order.getBillingAddress())).thenReturn(List.of(order));

        List<Order> actual = service.findByBillingAddress(order.getBillingAddress());

        assertEquals(1, actual.size());
        verify(orderRepository).findByBillingAddress(order.getBillingAddress());
    }

    @Test
    void shouldFindByTotalAmountGreaterThan() {
        BigDecimal amount = new BigDecimal(200);
        when(orderRepository.findByTotalAmountGreaterThan(amount)).thenReturn(List.of(order));

        List<Order> actual = service.findByTotalAmountGreaterThan(amount);

        assertEquals(1, actual.size());
        verify(orderRepository).findByTotalAmountGreaterThan(amount);
    }

    @Test
    void shouldFindByTotalAmountLessThan() {
        BigDecimal amount = new BigDecimal(2000);
        when(orderRepository.findByTotalAmountLessThan(amount)).thenReturn(List.of(order));

        List<Order> actual = service.findByTotalAmountLessThan(amount);

        assertEquals(1, actual.size());
        verify(orderRepository).findByTotalAmountLessThan(amount);
    }

    @Test
    void shouldFindById() {
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        Optional<Order> actual = service.findById(order.getOrderId());

        assertTrue(actual.isPresent());
        verify(orderRepository).findById(order.getOrderId());
    }

    @Test
    void shouldCreateValid() {
        Order mockOrder = new Order(order);
        order.setOrderId(0);

        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        Result<Order> result = service.create(order);

        assertEquals(ResultType.SUCCESS, result.getType());
    }

    @Test
    void shouldNotCreateInvalid() {

        when(addressRepository.findById(order.getShippingAddress().getAddressId())).thenReturn(Optional.of(address));
        when(appUserRepository.findById(order.getUser().getAppUserId())).thenReturn(Optional.of(user));

        Result<Order> actual = service.create(order);
        assertEquals(ResultType.INVALID, actual.getType());

        order.setOrderId(0);
        order.setUser(null);
        actual = service.create(order);
        assertEquals(ResultType.INVALID, actual.getType());

        order.setUser(user);
        order.setBillingAddress(null);
        actual = service.create(order);
        assertEquals(ResultType.INVALID, actual.getType());

        order.setBillingAddress(address);
        order.setShippingAddress(null);
        actual = service.create(order);
        assertEquals(ResultType.INVALID, actual.getType());

        order.setShippingAddress(address);
        order = null;
        actual = service.create(order);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotUpdateMissing() {
        order.setOrderStatus(OrderStatus.SUCCESS);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(addressRepository.findById(order.getShippingAddress().getAddressId())).thenReturn(Optional.of(address));
        when(appUserRepository.findById(order.getUser().getAppUserId())).thenReturn(Optional.of(user));


        Result<Order> actual = service.update(order);

        assertEquals(ResultType.SUCCESS, actual.getType());
    }
}
