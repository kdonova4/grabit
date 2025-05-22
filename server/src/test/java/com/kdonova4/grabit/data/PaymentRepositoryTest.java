package com.kdonova4.grabit.data;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class PaymentRepositoryTest {

    @Autowired
    PaymentRepository repository;

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

        Optional<Payment> payment = repository.findByOrder(order.get());

        assertNotNull(payment.get());
    }

    @Test
    void shouldFindByAmountPaidGreaterThan() {
        List<Payment> payments = repository.findByAmountPaidGreaterThan(new BigDecimal(1000));
        assertEquals(1, payments.size());
    }

    @Test
    void shouldFindByAmountPaidLessThan() {
        List<Payment> payments = repository.findByAmountPaidLessThan(new BigDecimal(2000));
        assertEquals(1, payments.size());
    }

    @Test
    void shouldCreate() {
        Optional<Order> order = orderRepository.findById(1);
        Payment payment = new Payment(0, order.get(), new BigDecimal(100), Timestamp.valueOf(LocalDateTime.now()));
        repository.save(payment);
        assertEquals(2, repository.findAll().size());
    }
}
