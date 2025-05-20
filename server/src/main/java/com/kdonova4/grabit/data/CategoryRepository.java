package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.Category;

import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findByCategoryName(String categoryName);
}
