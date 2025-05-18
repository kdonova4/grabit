package com.kdonova4.grabit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @Column(name = "shopping_cart_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int shoppingCartId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private int quantity;


}
