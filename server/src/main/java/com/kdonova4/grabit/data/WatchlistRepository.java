package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {

    List<Watchlist> findByUser(AppUser user);

    Optional<Watchlist> findByUserAndProduct(AppUser user, Product product);
}
