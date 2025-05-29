package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByPostedBy(AppUser postedBy);

    List<Review> findByProduct(Product product);

    List<Review> findBySeller(AppUser seller);

    List<Review> findByPostedByAndSeller(AppUser postedBy, AppUser seller);

    Optional<Review> findByPostedByAndProduct(AppUser postedBy, Product product);
}
