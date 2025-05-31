package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.data.ShipmentRepository;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.enums.ShipmentStatus;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.model.Shipment;
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

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    ShipmentRepository shipmentRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    ShipmentService service;

    private Order order;
    private Shipment shipment;

    @BeforeEach
    void setup() {
        order = new Order(0, null, Timestamp.valueOf(LocalDateTime.now()), null, null, new BigDecimal(2000), OrderStatus.PENDING, new ArrayList<>());
        shipment = new Shipment(0, order, ShipmentStatus.PENDING, "TRACK56789", Timestamp.valueOf(LocalDateTime.now()), null);
    }

    @Test
    void shouldFindAll() {
        when(shipmentRepository.findAll()).thenReturn(List.of(shipment));

        List<Shipment> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(shipmentRepository).findAll();
    }
}
