package com.kdonova4.grabit.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "review")
public class Review {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;

    @Column(nullable = false)
    private int rating;

    @Column(name = "review_text", nullable = false)
    private String reviewText;

    @ManyToOne(optional = false)
    @JoinColumn(name = "posted_by_id", nullable = false)
    private AppUser postedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private AppUser seller;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Generated(event = EventType.INSERT)
    @Column(name="created_at", nullable = false, updatable = false, insertable = false)
    private Timestamp createdAt;

}
