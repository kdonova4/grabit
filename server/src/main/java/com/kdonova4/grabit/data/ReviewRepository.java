package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByPostedBy(AppUser postedBy);

    List<Review> findByProduct(Product product);

    List<Review> findBySeller(AppUser seller);

}
