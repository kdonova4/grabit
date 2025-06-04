package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.CategoryRepository;
import com.kdonova4.grabit.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService service;

    private Category category;

    @BeforeEach
    void setup() {
        category = new Category(1, "Electronics");
    }

    @Test
    void shouldFindAll() {
        when(categoryRepository.findAll()).thenReturn(
                List.of(
                        category
                )
        );

        List<Category> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldFindByCategoryName() {
        when(categoryRepository.findByCategoryName("Electronics")).thenReturn(Optional.of(category));

        Optional<Category> actual = service.findByCategoryName("Electronics");

        assertTrue(actual.isPresent());
    }

    @Test
    void shouldFindById() {
        when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));

        Optional<Category> actual = service.findById(category.getCategoryId());

        assertTrue(actual.isPresent());
    }

    @Test
    void shouldCreate() {
        Category mockOut = category;
        mockOut.setCategoryId(1);
        category.setCategoryId(0);

        when(categoryRepository.save(category)).thenReturn(mockOut);

        Result<Category> actual = service.create(category);

        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {

        Result<Category> actual = service.create(category);
        assertEquals(ResultType.INVALID, actual.getType());

        category.setCategoryId(0);
        category.setCategoryName(null);
        actual = service.create(category);
        assertEquals(ResultType.INVALID, actual.getType());

        category.setCategoryName("Electronics");
        when(categoryRepository.findByCategoryName(category.getCategoryName())).thenReturn(Optional.of(category));
        actual = service.create(category);
        assertEquals(ResultType.INVALID, actual.getType());

        category.setCategoryName("Test");
        category = null;
        actual = service.create(category);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldDeleteById() {
        when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(category.getCategoryId());
        assertTrue(service.deleteById(category.getCategoryId()));
        verify(categoryRepository).deleteById(category.getCategoryId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(categoryRepository, never()).deleteById(anyInt());
    }


}
