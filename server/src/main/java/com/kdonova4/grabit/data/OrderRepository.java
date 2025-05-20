package com.kdonova4.grabit.data;

import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser(AppUser user);

    List<Order> findByOrderStatus(OrderStatus status);

    List<Order> findByOrderedAtAfter(Timestamp date);

    List<Order> findByOrderedAtBetween(Timestamp start, Timestamp end);

    List<Order> findByShippingAddress(Address shipingAddress);
    List<Order> findByBillingAddress(Address billingAddress);

    List<Order> findByTotalAmountGreaterThan(BigDecimal amount);
    List<Order> findByTotalAmountLessThan(BigDecimal amount);
}
