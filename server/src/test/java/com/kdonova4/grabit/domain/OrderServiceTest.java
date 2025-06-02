package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AddressRepository;
import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.OrderProductRepository;
import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private Shipment shipment;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setup() {

    }
}
