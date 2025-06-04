package com.kdonova4.grabit.model;

import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column(name="product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @Column(name="posted_at", nullable = false, updatable = false, insertable = false)
    private Timestamp postedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type", nullable = false)
    private SaleType saleType;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_condition", nullable = false)
    private ConditionType condition;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus productStatus;

    @Column(name = "auction_end")
    private LocalDateTime auctionEnd;

    @Column(name = "winning_bid")
    private BigDecimal winningBid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser user;

    public Product(Product source) {
        this.productId = source.getProductId();
        this.postedAt = source.getPostedAt();
        this.saleType = source.getSaleType();
        this.productName = source.getProductName();
        this.description = source.getDescription();
        this.price = source.getPrice();
        this.condition = source.getCondition();
        this.quantity = source.getQuantity();
        this.productStatus = source.getProductStatus();
        this.auctionEnd = source.getAuctionEnd();
        this.winningBid = source.getWinningBid();
        this.user = source.getUser();
    }
}
