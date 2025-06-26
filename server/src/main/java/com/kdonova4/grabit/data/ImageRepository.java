package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.Image;
import com.kdonova4.grabit.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    List<Image> findByProduct(Product product);
}
