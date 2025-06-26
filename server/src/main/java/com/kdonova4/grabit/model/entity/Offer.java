package com.kdonova4.grabit.model.entity;

import com.kdonova4.grabit.enums.OfferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "offer")
public class Offer {

    @Id
    @Column(name = "offer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int offerId;

    @Column(name = "offer_amount", nullable = false)
    private BigDecimal offerAmount;

    @Generated(event = EventType.INSERT)
    @Column(name="sent_at", nullable = false, updatable = false, insertable = false)
    private Timestamp sentAt;

    @Column(name = "offer_message")
    private String message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "app_user_id")
    private AppUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status", nullable = false)
    private OfferStatus offerStatus = OfferStatus.PENDING;
}
