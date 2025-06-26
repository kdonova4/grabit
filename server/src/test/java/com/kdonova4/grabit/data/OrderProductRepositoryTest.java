package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.OrderProduct;
import com.kdonova4.grabit.model.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class OrderProductRepositoryTest {

    @Autowired
    OrderProductRepository repository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByOrder() {
        Optional<Order> order = orderRepository.findById(1);

        List<OrderProduct> orderProducts = repository.findByOrder(order.get());

        assertEquals(1, orderProducts.size());
    }

    @Test
    void shouldFindByProduct() {
        Optional<Product> product = productRepository.findById(2);

        List<OrderProduct> orderProducts = repository.findByProduct(product.get());

        assertEquals(1, orderProducts.size());
    }

    @Test
    void shouldCreate() {
        Optional<Product> product = productRepository.findById(2);
        Optional<Order> order = orderRepository.findById(1);

        OrderProduct orderProduct = new OrderProduct(0, order.get(), product.get(), 1, new BigDecimal(1200), new BigDecimal(1200));

        repository.save(orderProduct);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);

        assertEquals(0, repository.findAll().size());
    }
}
