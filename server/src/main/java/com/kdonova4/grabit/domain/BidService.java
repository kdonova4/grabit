package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.BidRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Bid;
import com.kdonova4.grabit.model.Product;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BidService {
    private final BidRepository repository;
    private final AppUserRepository appUserRepository;
    private final ProductRepository productRepository;

    public BidService(BidRepository repository, AppUserRepository appUserRepository, ProductRepository productRepository) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
        this.productRepository = productRepository;
    }

    public List<Bid> findAll() {
        return repository.findAll();
    }

    public List<Bid> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    public List<Bid> findByProduct(Product product) {
        return repository.findByProduct(product);
    }

    public Optional<Bid> findById(int id) {
        return repository.findById(id);
    }

    public Result<Bid> create(Bid bid) {
        Result<Bid> result = validate(bid);

        if(!result.isSuccess()) {
            return result;
        }

        if(bid.getBidId() != 0) {
            result.addMessages("BidID CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        bid = repository.save(bid);
        result.setPayload(bid);
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

    private Result<Bid> validate(Bid bid) {
        Result<Bid> result = new Result<>();

        if(bid == null) {
            result.addMessages("RESULT CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(bid.getProduct() == null || bid.getProduct().getProductId() <= 0) {
            result.addMessages("PRODUCT IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(bid.getUser() == null || bid.getUser().getAppUserId() <= 0) {
            result.addMessages("USER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Product> product = productRepository.findById(bid.getProduct().getProductId());
        Optional<AppUser> appUser = appUserRepository.findById(bid.getUser().getAppUserId());

        if(product.isEmpty()) {
            result.addMessages("PRODUCT MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(appUser.isEmpty()) {
            result.addMessages("USER MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(bid.getPlacedAt() == null) {
            result.addMessages("PLACED AT CANNOT BE NULL", ResultType.INVALID);
        } else if(bid.getPlacedAt().after(Timestamp.valueOf(LocalDateTime.now()))) {
            result.addMessages("PLACED AT MUST BE IN THE PAST", ResultType.INVALID);
        }


        if(bid.getBidAmount().compareTo(bid.getProduct().getPrice()) <= 0) {
            result.addMessages("BID AMOUNT CANNOT BE EQUAL TO OR LESS THAN PRICE OF PRODUCT", ResultType.INVALID);
        }

        return result;
    }
}
