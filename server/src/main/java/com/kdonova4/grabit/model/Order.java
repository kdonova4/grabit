package com.kdonova4.grabit.model;

import com.kdonova4.grabit.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "purchase_order")
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser user;

    @Column(name="ordered_at", nullable = false, updatable = false, insertable = false)
    private Timestamp orderedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;

    @ManyToOne(optional = false)
    @JoinColumn(name = "billing_address_id", nullable = false)
    private Address billingAddress;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts;

    public Order(Order source) {
        this.orderId = source.getOrderId();
        this.user = source.getUser();
        this.shippingAddress = source.getShippingAddress();
        this.billingAddress = source.getBillingAddress();
        this.orderedAt = source.getOrderedAt();
        this.totalAmount = source.getTotalAmount();
        this.orderStatus = source.getOrderStatus();
        this.orderProducts = new ArrayList<>(source.getOrderProducts());
    }
}
