package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.OfferRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Offer;
import com.kdonova4.grabit.model.Product;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService {
    private final OfferRepository repository;
    private final AppUserRepository appUserRepository;
    private final ProductRepository productRepository;

    public OfferService(OfferRepository repository, AppUserRepository appUserRepository, ProductRepository productRepository) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
        this.productRepository = productRepository;
    }

    public List<Offer> findAll() {
        return repository.findAll();
    }

    public List<Offer> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    public List<Offer> findByProduct(Product product) {
        return repository.findByProduct(product);
    }

    public Optional<Offer> findByUserAndProduct(AppUser user, Product product) {
        return repository.findByUserAndProduct(user, product);
    }

    public List<Offer> findByProductAndExpireDateAfter(Product product, LocalDateTime now) {
        return repository.findByProductAndExpireDateAfter(product, now);
    }

    public Optional<Offer> findById(int id) {
        return repository.findById(id);
    }

    public Result<Offer> create(Offer offer) {
        Result<Offer> result = validate(offer);

        if(!result.isSuccess())
            return result;

        if(offer.getOfferId() != 0) {
            result.addMessages("OfferId CANNOT BE SET for 'add' operation", ResultType.INVALID);
        }

        offer = repository.save(offer);
        result.setPayload(offer);
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

    private Result<Offer> validate(Offer offer) {
        Result<Offer> result = new Result<>();

        if(offer == null) {
            result.addMessages("OFFER CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(offer.getProduct() == null || offer.getProduct().getProductId() <= 0) {
            result.addMessages("PRODUCT IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(offer.getUser() == null || offer.getUser().getAppUserId() <= 0) {
            result.addMessages("USER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Product> product = productRepository.findById(offer.getProduct().getProductId());
        Optional<AppUser> appUser = appUserRepository.findById(offer.getUser().getAppUserId());

        if(product.isEmpty()) {
            result.addMessages("PRODUCT MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(appUser.isEmpty()) {
            result.addMessages("USER MUST EXIST", ResultType.INVALID);
            return result;
        }

        Optional<Product> productOpt = productRepository.findById(offer.getProduct().getProductId());
        if (productOpt.isPresent() && productOpt.get().getSaleType() != SaleType.BUY_NOW) {
            result.addMessages("SALE TYPE MUST BE BUY NOW FOR OFFER TO BE PLACED", ResultType.INVALID);
        }

        if(offer.getSentAt() == null) {
            result.addMessages("SENT AT CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(offer.getSentAt().after(Timestamp.valueOf(LocalDateTime.now()))) {
            result.addMessages("SENT AT MUST BE IN THE PAST", ResultType.INVALID);
        }

        if(offer.getMessage().length() > 200) {
            result.addMessages("MESSAGE CANNOT BE LONGER THAN 200 CHARACTERS", ResultType.INVALID);
        }

        if(productOpt.isPresent()
                && productOpt.get().getPrice() != null
                && offer.getOfferAmount() != null
                && productOpt.get().getPrice().compareTo(offer.getOfferAmount()) == 0) {
            result.addMessages("OFFER AMOUNT CANNOT BE EQUAL TO THE PRICE OF THE PRODUCT", ResultType.INVALID);
        }

        return result;
    }
}
