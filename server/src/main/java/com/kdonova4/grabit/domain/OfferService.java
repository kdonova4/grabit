package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.OfferRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.domain.mapper.OfferMapper;
import com.kdonova4.grabit.enums.OfferStatus;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.dto.OfferCreateDTO;
import com.kdonova4.grabit.model.dto.OfferResponseDTO;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Offer;
import com.kdonova4.grabit.model.entity.Product;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Result<OfferResponseDTO> create(OfferCreateDTO offerCreateDTO) {

        AppUser appUser = appUserRepository.findById(offerCreateDTO.getUserId()).orElse(null);
        Product product = productRepository.findById(offerCreateDTO.getProductId()).orElse(null);

        Offer offer = OfferMapper.toOffer(offerCreateDTO, appUser, product);

        Result<OfferResponseDTO> result = validate(offer);

        if(!result.isSuccess())
            return result;

        if(offer.getOfferId() != 0) {
            result.addMessages("OfferId CANNOT BE SET for 'add' operation", ResultType.INVALID);
        }

        offer.setExpireDate(LocalDateTime.now().plusHours(48)); // or whatever your default is
        offer = repository.save(offer);
        result.setPayload(OfferMapper.toResponseDTO(offer));
        return result;
    }

    public boolean deleteById(int id) {
        if(repository.findById(id).isPresent() && repository.findById(id).get().getOfferStatus() != OfferStatus.ACCEPTED) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Result<Offer> rejectOffer(int offerId) {
        Optional<Offer> optional = findById(offerId);
        Result<Offer> result = new Result<>();

        if(optional.isEmpty()) {
            result.addMessages("OFFER NOT FOUND", ResultType.NOT_FOUND);
            return result;
        }

        Offer offer = optional.get();

        if(offer.getOfferStatus() == OfferStatus.EXPIRED) {
            result.addMessages("OFFER EXPIRED", ResultType.INVALID);
            return result;
        }

        offer.setOfferStatus(OfferStatus.REJECTED);
        Offer resultOffer = repository.save(offer);
        result.setPayload(resultOffer);
        return result;
    }

    @Transactional
    public Result<Offer> acceptOffer(int offerId) {
        Optional<Offer> optional = findById(offerId);
        Result<Offer> result = new Result<>();
        if (optional.isEmpty()) {
            result.addMessages("OFFER NOT FOUND", ResultType.NOT_FOUND);
            return result;
        }

        result = validateAcceptedOffer(optional.get());

        if(!result.isSuccess()) {
            return result;
        }

        Offer offer = optional.get();
        offer.setExpireDate(offer.getExpireDate().plusHours(8));
        offer.getProduct().setProductStatus(ProductStatus.HELD);
        offer.getProduct().setOfferPrice(offer.getOfferAmount());
        offer.setOfferStatus(OfferStatus.ACCEPTED);

        Offer resultOffer = repository.save(offer);
        productRepository.save(offer.getProduct());
        result.setPayload(resultOffer);
        return result;
    }

    private Result<Offer> validateAcceptedOffer(Offer offer) {
        Result<Offer> result = new Result<>();

        if(offer.getOfferStatus() == OfferStatus.EXPIRED) {
            result.addMessages("OFFER IS EXPIRED, CANNOT ACCEPT", ResultType.INVALID);
            return result;
        }

        if(offer.getProduct().getProductStatus() == ProductStatus.HELD) {
            result.addMessages("ANOTHER OFFER WAS ALREADY ACCEPTED", ResultType.INVALID);
        }

        return result;

    }

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void expireOffers() {
        List<Offer> expiredOffers = repository.findByExpireDateBefore(LocalDateTime.now());
        expiredOffers.forEach(offer -> {
            if(offer.getOfferStatus() == OfferStatus.ACCEPTED) {
                offer.setOfferStatus(OfferStatus.EXPIRED);
                offer.getProduct().setProductStatus(ProductStatus.ACTIVE);
                repository.save(offer);
                productRepository.save(offer.getProduct());
            } else {
                offer.setOfferStatus(OfferStatus.EXPIRED);
                repository.save(offer);
            }

        });
    }

    private Result<OfferResponseDTO> validate(Offer offer) {
        Result<OfferResponseDTO> result = new Result<>();

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

        if(offer.getMessage().length() > 200) {
            result.addMessages("MESSAGE CANNOT BE LONGER THAN 200 CHARACTERS", ResultType.INVALID);
        }

        if(appUser.get().getAppUserId() == productOpt.get().getUser().getAppUserId()) {
            result.addMessages("CANNOT MAKE AN OFFER ON YOUR OWN PRODUCT", ResultType.INVALID);
        }

        if(offer.getProduct().getProductStatus() != ProductStatus.ACTIVE) {
            result.addMessages("CANNOT SEND AN OFFER TO AN INACTIVE PRODUCT", ResultType.INVALID);
        }

        if(productOpt.isPresent()
                && productOpt.get().getPrice() != null
                && offer.getOfferAmount() != null
                && productOpt.get().getPrice().compareTo(offer.getOfferAmount()) == 0) {
            result.addMessages("OFFER AMOUNT CANNOT BE EQUAL TO THE PRICE OF THE PRODUCT", ResultType.INVALID);
        }

        Optional<Offer> productOffer = repository.findByUserAndProduct(appUser.get(), product.get());

        if(productOffer.isPresent()) {
            result.addMessages("CANNOT MAKE MORE THAN ONE OFFER AT A TIME ON A PRODUCT", ResultType.INVALID);
        }

        return result;
    }
}
