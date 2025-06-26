package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByOrder(Order order);

    List<Payment> findByAmountPaidGreaterThan(BigDecimal amount);

    List<Payment> findByAmountPaidLessThan(BigDecimal amount);

}
