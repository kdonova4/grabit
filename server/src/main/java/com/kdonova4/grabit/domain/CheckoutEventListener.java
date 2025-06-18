package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.enums.ShipmentStatus;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.model.OrderPlacedEvent;
import com.kdonova4.grabit.model.Shipment;
import com.kdonova4.grabit.model.ShipmentPlacedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CheckoutEventListener {

    private final OrderService orderService;
    private final ShipmentService shipmentService;


    public CheckoutEventListener(OrderService orderService, ShipmentService shipmentService) {
        this.orderService = orderService;
        this.shipmentService = shipmentService;
    }


    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        int orderId = event.getOrderId();
        System.out.println("Order Placed, ID: " + orderId);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(30_000);
                Optional<Order> order = orderService.findById(orderId);
                order.ifPresent(value -> value.setOrderStatus(OrderStatus.SUCCESS));
                orderService.update(order.get());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    @EventListener
    public void handleShipmentPlaced(ShipmentPlacedEvent event) {
        int shipmentId = event.getShipmentId();
        System.out.println("Shipment Placed, ID: " + shipmentId);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(30_000);

                Optional<Shipment> shipment = shipmentService.findById(shipmentId);
                Shipment actual = shipment.get();
                actual.setShipmentStatus(ShipmentStatus.SHIPPED);
                shipmentService.update(shipment.get());

                Thread.sleep(30_000);
                actual.setShipmentStatus(ShipmentStatus.IN_TRANSIT);
                shipmentService.update(shipment.get());

                Thread.sleep(30_000);
                actual.setShipmentStatus(ShipmentStatus.OUT_FOR_DELIVERY);
                shipmentService.update(shipment.get());

                Thread.sleep(30_000);
                actual.setShipmentStatus(ShipmentStatus.DELIVERED);
                actual.setDeliveredAt(Timestamp.valueOf(LocalDateTime.now()));
                shipmentService.update(actual);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
