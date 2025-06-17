package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Bid;
import com.kdonova4.grabit.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    List<Bid> findByUser(AppUser user);

    List<Bid> findByProduct(Product product);

    Optional<Bid> findFirstByUserAndProductOrderByBidAmountDesc(AppUser user, Product product);

    List<Bid> findByProductOrderByBidAmountDesc(Product product);
}
