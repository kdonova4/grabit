package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.data.ShoppingCartRepository;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;

    public ShoppingCartService(ShoppingCartRepository repository, ProductRepository productRepository, AppUserRepository appUserRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<ShoppingCart> findAll() {
        return repository.findAll();
    }

    public List<ShoppingCart> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    public Optional<ShoppingCart> findByUserAndProduct(AppUser user, Product product) {
        return repository.findByUserAndProduct(user, product);
    }

    public Optional<ShoppingCart> findById(int id) {
        return repository.findById(id);
    }

    public Result<ShoppingCart> create(ShoppingCart shoppingCart) {
        Result<ShoppingCart> result = validate(shoppingCart);

        if(!result.isSuccess())
            return result;

        if(shoppingCart.getShoppingCartId() != 0) {
            result.addMessages("ShoppingCartId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        shoppingCart = repository.save(shoppingCart);
        result.setPayload(shoppingCart);
        return result;
    }

    public void deleteByUser(AppUser user) {
        repository.deleteByUser(user);
    }

    public boolean deleteById(int id) {
        if(repository.findById(id).isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private Result<ShoppingCart> validate(ShoppingCart shoppingCart) {
        Result<ShoppingCart> result = new Result<>();

        if(shoppingCart == null) {
            result.addMessages("SHOPPING CART CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(shoppingCart.getProduct() == null || shoppingCart.getProduct().getProductId() <= 0) {
            result.addMessages("PRODUCT IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(shoppingCart.getUser() == null || shoppingCart.getUser().getAppUserId() <= 0) {
            result.addMessages("USER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Product> product = productRepository.findById(shoppingCart.getProduct().getProductId());
        Optional<AppUser> appUser = appUserRepository.findById(shoppingCart.getUser().getAppUserId());

        if(product.isEmpty()) {
            result.addMessages("PRODUCT MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(appUser.isEmpty()) {
            result.addMessages("USER MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(repository.findByUserAndProduct(shoppingCart.getUser(), shoppingCart.getProduct()).isPresent()) {
            result.addMessages("CANNOT ADD DUPLICATE ITEM TO CART", ResultType.INVALID);
        }

        if(shoppingCart.getQuantity() < 1) {
            result.addMessages("QUANTITY MUST BE 1 OR GREATER", ResultType.INVALID);
        }

        if(shoppingCart.getQuantity() > product.get().getQuantity()) {
            result.addMessages("QUANTITY CANNOT BE HIGHER THAN PRODUCT AVAILABLE", ResultType.INVALID);
        }

        if(product.get().getSaleType() == SaleType.AUCTION) {
            result.addMessages("CANNOT ADD AUCTION ITEMS TO CART", ResultType.INVALID);
        }

        return result;
    }

}
