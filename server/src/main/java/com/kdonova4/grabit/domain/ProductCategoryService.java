package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.CategoryRepository;
import com.kdonova4.grabit.data.ProductCategoryRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.model.Category;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.ProductCategory;

import java.util.List;
import java.util.Optional;

public class ProductCategoryService {

    private final ProductCategoryRepository repository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductCategoryService(ProductCategoryRepository repository, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductCategory> findAll() {
        return repository.findAll();
    }

    public List<ProductCategory> findByProduct(Product product) {
        return repository.findByProduct(product);
    }

    public List<ProductCategory> findByCategory(Category category) {
        return repository.findByCategory(category);
    }

    public Optional<ProductCategory> findByCategoryAndProduct(Category category, Product product) {
        return repository.findByCategoryAndProduct(category, product);
    }

    public Optional<ProductCategory> findById(int id) {
        return repository.findById(id);
    }

    public Result<ProductCategory> create(ProductCategory productCategory) {
        Result<ProductCategory> result = validate(productCategory);

        if(!result.isSuccess())
            return result;

        if(productCategory.getProductCategoryId() != 0) {
            result.addMessages("ProductCategoryId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        productCategory = repository.save(productCategory);
        result.setPayload(productCategory);
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

    private Result<ProductCategory> validate(ProductCategory productCategory) {
        Result<ProductCategory> result = new Result<>();

        if(productCategory == null) {
            result.addMessages("PRODUCT CATEGORY CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(productCategory.getProduct() == null || productCategory.getProduct().getProductId() <= 0) {
            result.addMessages("PRODUCT IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(productCategory.getCategory() == null || productCategory.getCategory().getCategoryId() <= 0) {
            result.addMessages("CATEGORY IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Product> product = productRepository.findById(productCategory.getProduct().getProductId());
        Optional<Category> category = categoryRepository.findById(productCategory.getCategory().getCategoryId());

        if(product.isEmpty()) {
            result.addMessages("PRODUCT MUST EXIST", ResultType.INVALID);
        }

        if(category.isEmpty()) {
            result.addMessages("CATEGORY MUST EXIST", ResultType.INVALID);
        }

        return result;
    }

}
