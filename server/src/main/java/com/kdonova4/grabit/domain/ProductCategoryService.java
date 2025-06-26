package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.CategoryRepository;
import com.kdonova4.grabit.data.ProductCategoryRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.domain.mapper.ProductCategoryMapper;
import com.kdonova4.grabit.model.dto.ProductCategoryCreateDTO;
import com.kdonova4.grabit.model.dto.ProductCategoryResponseDTO;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ProductCategory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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

    public Result<ProductCategoryResponseDTO> create(ProductCategoryCreateDTO productCategoryCreateDTO) {

        Product product = productRepository.findById(productCategoryCreateDTO.getProductId()).orElse(null);
        Category category = categoryRepository.findById(productCategoryCreateDTO.getCategoryId()).orElse(null);

        ProductCategory productCategory = ProductCategoryMapper.toProductCategory(product, category);

        Result<ProductCategoryResponseDTO> result = validate(productCategory);

        if(!result.isSuccess())
            return result;

        if(productCategory.getProductCategoryId() != 0) {
            result.addMessages("ProductCategoryId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        productCategory = repository.save(productCategory);
        result.setPayload(ProductCategoryMapper.toResponseDTO(productCategory));
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

    private Result<ProductCategoryResponseDTO> validate(ProductCategory productCategory) {
        Result<ProductCategoryResponseDTO> result = new Result<>();

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

        if(repository.findByCategoryAndProduct(productCategory.getCategory(), productCategory.getProduct()).isPresent()) {
            result.addMessages("CANNOT ADD DUPLICATE CATEGORY", ResultType.INVALID);
        }

        return result;
    }

}
