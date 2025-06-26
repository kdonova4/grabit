package com.kdonova4.grabit.data;
import com.kdonova4.grabit.enums.ShipmentStatus;
import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.Shipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class ShipmentRepositoryTest {

    @Autowired
    ShipmentRepository repository;

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

        Optional<Shipment> shipment = repository.findByOrder(order.get());

        assertNotNull(shipment.get());
    }

    @Test
    void shouldFindByShipmentStatus() {
        List<Shipment> shipments = repository.findByShipmentStatus(ShipmentStatus.PENDING);
        assertEquals(1, shipments.size());
    }

    @Test
    void shouldFindByTrackingNumber() {
        Optional<Shipment> shipments = repository.findByTrackingNumber("TRACK12345");
        assertNotNull(shipments.get());
    }

    @Test
    void shouldCreate() {
        Optional<Order> order = orderRepository.findById(1);
        Shipment shipment = new Shipment(0, order.get(), ShipmentStatus.PENDING, "TRACK56789", Timestamp.valueOf(LocalDateTime.now()), null);
        repository.save(shipment);
        assertEquals(2, repository.findAll().size());
    }
}
