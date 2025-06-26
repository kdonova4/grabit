package com.kdonova4.grabit.data;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.model.entity.Address;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class OrderRepositoryTest {

    @Autowired
    OrderRepository repository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByUser() {
        Optional<AppUser> user = appUserRepository.findById(1);

        List<Order> orders = repository.findByUser(user.get());

        assertEquals(1, orders.size());
    }

    @Test
    void shouldFindByOrderStatus() {
        List<Order> orders = repository.findByOrderStatus(OrderStatus.PENDING);

        assertEquals(1, orders.size());
    }

    @Test
    void shouldFindByOrderedAtAfter() {
        List<Order> orders = repository.findByOrderedAtAfter(Timestamp.valueOf(LocalDateTime.now().minusDays(5)));
        assertEquals(1, orders.size());
    }

    @Test
    void shouldFindByOrderedAtBetween() {
        List<Order> orders = repository.findByOrderedAtBetween(Timestamp.valueOf(LocalDateTime.now().minusDays(5)), Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
        assertEquals(1, orders.size());
    }

    @Test
    void shouldFindByShippingAddress() {
        Optional<Address> address = addressRepository.findById(1);

        List<Order> orders = repository.findByShippingAddress(address.get());
        assertEquals(1, orders.size());
    }

    @Test
    void shouldFindByBillingAddress() {
        Optional<Address> address = addressRepository.findById(1);

        List<Order> orders = repository.findByBillingAddress(address.get());
        assertEquals(1, orders.size());
    }

    @Test
    void shouldFindByTotalAmountGreaterThan() {
        List<Order> orders = repository.findByTotalAmountGreaterThan(new BigDecimal(1000));
        assertEquals(1, orders.size());
    }

    @Test
    void shouldFindByTotalAmountLessThan() {
        List<Order> orders = repository.findByTotalAmountLessThan(new BigDecimal(1600));
        assertEquals(1, orders.size());
    }

    @Test
    void shouldCreate() {
        Optional<AppUser> user = appUserRepository.findById(1);
        Optional<Address> address = addressRepository.findById(1);

        Order order = new Order(0, user.get(), Timestamp.valueOf(LocalDateTime.now()), address.get(), address.get(), new BigDecimal(2000), OrderStatus.PENDING, new ArrayList<>());

        repository.save(order);

        assertEquals(2, repository.findAll().size());
    }


}
