package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.BidRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.domain.mapper.BidMapper;
import com.kdonova4.grabit.model.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

@Service
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

    public List<Bid> findByProductOrderByBidAmountDesc(Product product) {
        return repository.findByProductOrderByBidAmountDesc(product);
    }

    public Optional<Bid> findFirstByUserAndProductOrderByBidAmountDesc(AppUser user, Product product) {
        return repository.findFirstByUserAndProductOrderByBidAmountDesc(user, product);
    }

    public Optional<Bid> findById(int id) {
        return repository.findById(id);
    }

    public Result<BidResponseDTO> create(BidCreateDTO bid) {

        Product product = productRepository.findById(bid.getProductId()).orElse(null);
        AppUser user = appUserRepository.findById(bid.getUserId()).orElse(null);

        Bid actual = BidMapper.toBid(bid, product, user);

        Result<BidResponseDTO> result = validate(actual);

        if(!result.isSuccess()) {
            return result;
        }

        if(actual.getBidId() != 0) {
            result.addMessages("BidID CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        actual = repository.save(actual);
        result.setPayload(BidMapper.toResponseDTO(actual));
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

    private Result<BidResponseDTO> validate(Bid bid) {
        Result<BidResponseDTO> result = new Result<>();

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

        Optional<Bid> highestUserBid = findFirstByUserAndProductOrderByBidAmountDesc(appUser.get(), product.get());

        if(highestUserBid.isPresent()) {
            if(bid.getBidAmount().compareTo(highestUserBid.get().getBidAmount()) <= 0) {
                result.addMessages("BID AMOUNT CANNOT BE LESS THAN OR EQUAL TO PREVIOUS BIDS", ResultType.INVALID);
            }
        }



        return result;
    }
}
