package com.kdonova4.grabit.model.entity;

import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Generated(event = EventType.INSERT)
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
    private ProductStatus productStatus = ProductStatus.ACTIVE;

    @Column(name = "auction_end")
    private LocalDateTime auctionEnd;

    @Column(name = "winning_bid")
    private BigDecimal winningBid;

    @Column(name = "offer_price")
    private BigDecimal offerPrice;

    @ManyToMany
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser user;

    public Product(int productId, Timestamp postedAt, SaleType saleType, String productName,
                   String description, BigDecimal price, ConditionType condition, int quantity,
                   ProductStatus productStatus, LocalDateTime auctionEnd, BigDecimal winningBid,
                   BigDecimal offerPrice, AppUser user) {
        this.productId = productId;
        this.postedAt = postedAt;
        this.saleType = saleType;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.condition = condition;
        this.quantity = quantity;
        this.productStatus = productStatus;
        this.auctionEnd = auctionEnd;
        this.winningBid = winningBid;
        this.offerPrice = offerPrice;
        this.user = user;
    }

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
        this.offerPrice = source.getOfferPrice();
        this.user = source.getUser();
    }
}
