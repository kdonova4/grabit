package com.kdonova4.grabit.data;

import com.kdonova4.grabit.enums.OfferStatus;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Offer;
import com.kdonova4.grabit.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Integer> {

    List<Offer> findByUser(AppUser user);

    List<Offer> findByProduct(Product product);

    Optional<Offer> findByUserAndProduct(AppUser user, Product product);

    List<Offer> findByProductAndExpireDateAfter(Product product, LocalDateTime now);

    List<Offer> findByExpireDateBefore(LocalDateTime now);

    @Query("""
    SELECT o.product
    FROM Offer o
    WHERE o.user.appUserId = :userId
      AND o.offerStatus = :status
""")
    List<Product> findProductsByUserIdAndOfferStatus(@Param("userId") int userId, @Param("status") OfferStatus status);



}
