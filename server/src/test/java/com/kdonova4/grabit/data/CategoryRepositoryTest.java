package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByCategoryName() {

        Optional<Category> category = repository.findByCategoryName("Books");

        assertNotNull(category.get());
    }

    @Test
    void shouldCreate() {
        Category category = new Category(0, "Toys");

        repository.save(category);

        assertEquals(4, repository.findAll().size());
    }

    @Test
    void shouldUpdate() {
        Optional<Category> category = repository.findByCategoryName("Books");

        category.get().setCategoryName("Reading");
        repository.save(category.get());
        assertTrue(repository.findByCategoryName("Reading").isPresent());
    }

    @Test
    void shouldDelete() {
        repository.deleteById(1);

        assertEquals(2, repository.findAll().size());
    }
}