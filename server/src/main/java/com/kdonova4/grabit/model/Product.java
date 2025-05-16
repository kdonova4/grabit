package com.kdonova4.grabit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(name="productId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int productId;
}
