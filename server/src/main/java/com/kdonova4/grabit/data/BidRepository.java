package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Bid;
import com.kdonova4.grabit.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    List<Bid> findByUser(AppUser user);

    List<Bid> findByProduct(Product product);

    Optional<Bid> findFirstByUserAndProductOrderByBidAmountDesc(AppUser user, Product product);

    List<Bid> findByProductOrderByBidAmountDesc(Product product);
}
