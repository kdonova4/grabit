package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Offer;
import com.kdonova4.grabit.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Integer> {

    List<Offer> findByUser(AppUser user);

    List<Offer> findByProduct(Product product);

    Optional<Offer> findByUserAndProduct(AppUser user, Product product);

    List<Offer> findByProductAndExpireDateAfter(Product product, LocalDateTime now);
}
