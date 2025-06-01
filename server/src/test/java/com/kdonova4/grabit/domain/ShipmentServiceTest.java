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
import java.util.Optional;

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
        order = new Order(1, null, Timestamp.valueOf(LocalDateTime.now()), null, null, new BigDecimal(2000), OrderStatus.PENDING, new ArrayList<>());
        shipment = new Shipment(1, order, ShipmentStatus.PENDING, "TRACKTRACKTRACK123", Timestamp.valueOf(LocalDateTime.now()), null);
    }

    @Test
    void shouldFindAll() {
        when(shipmentRepository.findAll()).thenReturn(List.of(shipment));

        List<Shipment> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(shipmentRepository).findAll();
    }

    @Test
    void shouldFindByOrder() {
        when(shipmentRepository.findByOrder(order)).thenReturn(Optional.of(shipment));

        Optional<Shipment> actual = service.findByOrder(order);

        assertTrue(actual.isPresent());
        verify(shipmentRepository).findByOrder(order);
    }

    @Test
    void shouldFindByShipmentStatus() {
        when(shipmentRepository.findByShipmentStatus(shipment.getShipmentStatus())).thenReturn(List.of(shipment));

        List<Shipment> actual = service.findByShipmentStatus(shipment.getShipmentStatus());

        assertEquals(1, actual.size());
        verify(shipmentRepository).findByShipmentStatus(shipment.getShipmentStatus());
    }

    @Test
    void shouldFindByTrackingNumber() {
        when(shipmentRepository.findByTrackingNumber(shipment.getTrackingNumber())).thenReturn(Optional.of(shipment));

        Optional<Shipment> actual = service.findByTrackingNumber(shipment.getTrackingNumber());

        assertTrue(actual.isPresent());
        verify(shipmentRepository).findByTrackingNumber(shipment.getTrackingNumber());
    }

    @Test
    void shouldCreateValid() {
        Shipment mockOut = shipment;
        shipment.setShipmentId(0);

        when(shipmentRepository.save(shipment)).thenReturn(mockOut);
        when(orderRepository.findById(shipment.getOrder().getOrderId())).thenReturn(Optional.of(order));

        Result<Shipment> actual = service.create(shipment);
        System.out.println(actual.getMessages());
        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {

        when(orderRepository.findById(shipment.getOrder().getOrderId())).thenReturn(Optional.of(order));


        Result<Shipment> actual = service.create(shipment);
        assertEquals(ResultType.INVALID, actual.getType());

        shipment.setShipmentId(0);
        shipment.setShipmentStatus(null);
        actual = service.create(shipment);
        assertEquals(ResultType.INVALID, actual.getType());

        shipment.setShipmentStatus(ShipmentStatus.PENDING);
        shipment.setShippedAt(Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
        actual = service.create(shipment);
        assertEquals(ResultType.INVALID, actual.getType());

        shipment.setShippedAt(Timestamp.valueOf(LocalDateTime.now()));
        shipment.setTrackingNumber("");
        actual = service.create(shipment);
        assertEquals(ResultType.INVALID, actual.getType());

        shipment = null;
        actual = service.create(shipment);
        assertEquals(ResultType.INVALID, actual.getType());

    }
}
