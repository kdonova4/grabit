package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

    List<ShoppingCart> findByUser(AppUser user);

    Optional<ShoppingCart> findByUserAndProduct(AppUser user, Product product);

    void deleteByUser(AppUser user);
}
