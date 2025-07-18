package com.kdonova4.grabit.model.entity;

import com.kdonova4.grabit.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;


import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "shipment")
public class Shipment {

    @Id
    @Column(name = "shipment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shipmentId;

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false)
    private ShipmentStatus shipmentStatus = ShipmentStatus.PENDING;

    @Column(name = "tracking_number", unique = true, nullable = false)
    private String trackingNumber;

    @Generated(event = EventType.INSERT)
    @Column(name="shipped_at", nullable = false, updatable = false, insertable = false)
    private Timestamp shippedAt;

    @Column(name="delivered_at", insertable = false)
    private Timestamp deliveredAt;
}
