package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.data.WatchlistRepository;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.ShoppingCart;
import com.kdonova4.grabit.model.Watchlist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WatchlistService {

    private final WatchlistRepository repository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;

    public WatchlistService(WatchlistRepository repository, ProductRepository productRepository, AppUserRepository appUserRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<Watchlist> findAll() {
        return repository.findAll();
    }

    public List<Watchlist> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    public Optional<Watchlist> findByUserAndProduct(AppUser user, Product product) {
        return repository.findByUserAndProduct(user, product);
    }

    public Optional<Watchlist> findById(int id) {
        return repository.findById(id);
    }

    public Result<Watchlist> create(Watchlist watchlist) {
        Result<Watchlist> result = validate(watchlist);

        if(!result.isSuccess())
            return result;

        if(watchlist.getWatchId() != 0) {
            result.addMessages("WatchId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        watchlist = repository.save(watchlist);
        result.setPayload(watchlist);
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

    private Result<Watchlist> validate(Watchlist watchlist) {
        Result<Watchlist> result = new Result<>();

        if(watchlist == null) {
            result.addMessages("WATCHLIST CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(watchlist.getProduct() == null || watchlist.getProduct().getProductId() <= 0) {
            result.addMessages("PRODUCT IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(watchlist.getUser() == null || watchlist.getUser().getAppUserId() <= 0) {
            result.addMessages("USER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Product> product = productRepository.findById(watchlist.getProduct().getProductId());
        Optional<AppUser> appUser = appUserRepository.findById(watchlist.getUser().getAppUserId());

        if(product.isEmpty()) {
            result.addMessages("PRODUCT MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(appUser.isEmpty()) {
            result.addMessages("USER MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(repository.findByUserAndProduct(watchlist.getUser(), watchlist.getProduct()).isPresent()) {
            result.addMessages("CANNOT ADD DUPLICATE ITEM TO WATCHLIST", ResultType.INVALID);
        }

        return result;
    }
}
