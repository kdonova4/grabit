package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.data.PaymentRepository;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.Payment;
import com.kdonova4.grabit.model.dto.PaymentResponseDTO;
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

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    PaymentService service;

    private Order order;
    private Payment payment;

    @BeforeEach
    void setup() {
        order = new Order(1, null, Timestamp.valueOf(LocalDateTime.now()), null, null, new BigDecimal(100), OrderStatus.PENDING, new ArrayList<>());
        payment = new Payment(1, order, new BigDecimal(100), Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    void shouldFindALl() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<Payment> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(paymentRepository).findAll();
    }

    @Test
    void shouldFindByOrder() {
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));

        Optional<Payment> actual = service.findByOrder(order);

        assertTrue(actual.isPresent());
        verify(paymentRepository).findByOrder(order);
    }

    @Test
    void shouldFindByAmountPaidGreaterThan() {
        BigDecimal amount = new BigDecimal(50);
        when(paymentRepository.findByAmountPaidGreaterThan(amount)).thenReturn(List.of(payment));

        List<Payment> actual = service.findByAmountPaidGreaterThan(amount);

        assertEquals(1, actual.size());
        verify(paymentRepository).findByAmountPaidGreaterThan(amount);
    }

    @Test
    void shouldFindByAmountPaidLessThan() {
        BigDecimal amount = new BigDecimal(500);
        when(paymentRepository.findByAmountPaidLessThan(amount)).thenReturn(List.of(payment));

        List<Payment> actual = service.findByAmountPaidLessThan(amount);

        assertEquals(1, actual.size());
        verify(paymentRepository).findByAmountPaidLessThan(amount);
    }

    @Test
    void shouldCreateValid() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        Payment mockOut = new Payment(1, order, payment.getAmountPaid(), timestamp);

        payment.setPaymentId(0);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockOut);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Result<PaymentResponseDTO> actual = service.create(payment);

        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotCreateInvalid() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Result<PaymentResponseDTO> actual = service.create(payment);
        assertEquals(ResultType.INVALID, actual.getType());

        payment.setPaymentId(0);
        payment.setAmountPaid(null);
        actual = service.create(payment);
        assertEquals(ResultType.INVALID, actual.getType());

        payment.setAmountPaid(new BigDecimal(500));
        actual = service.create(payment);
        assertEquals(ResultType.INVALID, actual.getType());

        payment.setAmountPaid(new BigDecimal(100));
        payment = null;
        actual = service.create(payment);
        assertEquals(ResultType.INVALID, actual.getType());

    }
}
