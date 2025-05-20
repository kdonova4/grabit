package com.kdonova4.grabit.data;

import com.kdonova4.grabit.enums.ShipmentStatus;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

    Optional<Shipment> findByOrder(Order order);

    List<Shipment> findByShipmentStatus(ShipmentStatus status);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
