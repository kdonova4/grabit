package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.CategoryRepository;
import com.kdonova4.grabit.model.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository repository;


    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Optional<Category> findByCategoryName(String name) {
        return repository.findByCategoryName(name);
    }

    public Optional<Category> findById(int id) {
        return repository.findById(id);
    }

    public Result<Category> create(Category category) {
        Result<Category> result = validate(category);

        if(!result.isSuccess()) {
            return result;
        }

        if(category.getCategoryId() != 0) {
            result.addMessages("CategoryId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        category = repository.save(category);
        result.setPayload(category);
        return result;
    }

    public boolean deleteById(int id) {
        if(repository.findById(id).isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private Result<Category> validate(Category category) {
        Result<Category> result = new Result<>();

        if(category == null) {
            result.addMessages("CATEGORY CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(category.getCategoryName() == null || category.getCategoryName().isBlank()) {
            result.addMessages("CATEGORY NAME CANNOT BE NULL OR BLANK", ResultType.INVALID);
        }

        if(repository.findByCategoryName(category.getCategoryName()).isPresent()) {
            result.addMessages("CATEGORY ALREADY EXISTS", ResultType.INVALID);
        }

        return result;
    }
}
