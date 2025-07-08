package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.AppUserRepository;
import com.kdonova4.grabit.data.BidRepository;
import com.kdonova4.grabit.data.CategoryRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.domain.mapper.ProductMapper;
import com.kdonova4.grabit.enums.ConditionType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.dto.*;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Bid;
import com.kdonova4.grabit.model.entity.Category;
import com.kdonova4.grabit.model.entity.Product;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final AppUserRepository appUserRepository;
    private final CategoryRepository categoryRepository;
    private final BidRepository bidRepository;

    private final ShoppingCartService shoppingCartService;
    private final AddressService addressService;

    public ProductService(ProductRepository repository, AppUserRepository appUserRepository, CategoryRepository categoryRepository, BidRepository bidRepository, ShoppingCartService shoppingCartService, AddressService addressService) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
        this.categoryRepository = categoryRepository;
        this.bidRepository = bidRepository;
        this.shoppingCartService = shoppingCartService;
        this.addressService = addressService;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public List<Product> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    public List<Product> search(String productName,
                                BigDecimal minPrice,
                                BigDecimal maxPrice,
                                ProductStatus status,
                                ConditionType condition,
                                SaleType saleType,
                                Category category) {
        return repository.search(productName, minPrice, maxPrice, status, condition, saleType, category);
    }

    public Optional<Product> findById(int id) {
        return repository.findById(id);
    }

    public List<Product> findBySaleTypeAndProductStatusAndAuctionEndBefore(SaleType saleType, ProductStatus status, LocalDateTime now) {
        return repository.findBySaleTypeAndProductStatusAndAuctionEndBefore(saleType, status, now);
    }

    public List<Product> findAllByCategoryId(int categoryId) {
        return repository.findAllByCategoryId(categoryId);
    }

    public Result<ProductResponseDTO> create(ProductCreateDTO productCreateDTO) {

        AppUser user = appUserRepository.findById(productCreateDTO.getUserId()).orElse(null);

        Product product = ProductMapper.toProduct(productCreateDTO, user);

        List<Category> categories = categoryRepository.findAllById(productCreateDTO.getCategoryIds());
        product.setCategories(categories);

        Result<ProductResponseDTO> result = validate(product);

        if(!result.isSuccess())
            return result;

        if(product.getProductId() != 0) {
            result.addMessages("ProductId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        if(product.getSaleType() == SaleType.AUCTION)
            product.setAuctionEnd(LocalDateTime.now().plusSeconds(45));

        product = repository.save(product);

        result.setPayload(ProductMapper.toResponseDTO(product));

        return result;
    }

    public Result<ProductResponseDTO> update(ProductUpdateDTO productUpdateDTO) {

        Product oldProduct = repository.findById(productUpdateDTO.getProductId()).orElse(null);

        if(oldProduct == null) {
            Result<ProductResponseDTO> result = new Result<>();
            result.addMessages("Old Product Not Found", ResultType.NOT_FOUND);
            return result;
        }

        Product product = ProductMapper.toProduct(productUpdateDTO, oldProduct);
        List<Category> categories = categoryRepository.findAllById(productUpdateDTO.getCategoryIds());
        product.setCategories(categories);

        Result<ProductResponseDTO> result = validate(product);

        if(!result.isSuccess())
            return result;

        if(product.getProductId() <= 0) {
            result.addMessages("ProductId MUST BE SET for 'update' operation", ResultType.INVALID);
            return result;
        }

        Optional<Product> optProduct = repository.findById(product.getProductId());
        if(optProduct.isPresent()) {
            repository.save(product);
        } else {
            result.addMessages("Product " + product.getProductId() + " Not Found", ResultType.INVALID);
            return result;
        }

        return result;
    }

    public boolean deleteById(int id) {
        if(repository.findById(id).isPresent() && repository.findById(id).get().getProductStatus() != ProductStatus.SOLD) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }



    private Result<ProductResponseDTO> validate(Product product) {
        Result<ProductResponseDTO> result = new Result<>();



        if(product == null) {
            result.addMessages("PRODUCT CANNOT BE NULL", ResultType.INVALID);
            return result;
        }


        if(product.getUser() == null || product.getUser().getAppUserId() <= 0) {
            result.addMessages("USER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<AppUser> appUser = appUserRepository.findById(product.getUser().getAppUserId());

        if(appUser.isEmpty()) {
            result.addMessages("USER MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(product.getPrice() == null) {
            result.addMessages("PRODUCT PRICE IS REQUIRED", ResultType.INVALID);
        } else if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            result.addMessages("PRODUCT PRICE MUST BE GREATER THAN ZERO", ResultType.INVALID);
        }


        if(product.getProductName() == null || product.getProductName().isBlank()) {
            result.addMessages("PRODUCT NAME CANNOT BE NULL OR BLANK", ResultType.INVALID);
        } else if (product.getProductName().length() > 100) {
            result.addMessages("PRODUCT NAME CANNOT BE GREATER THAN 100 CHARACTERS", ResultType.INVALID);
        }

        if(product.getDescription() == null || product.getDescription().isBlank()) {
            result.addMessages("PRODUCT DESCRIPTION CANNOT BE NULL OR BLANK", ResultType.INVALID);
        } else if (product.getDescription().length() > 500) {
            result.addMessages("PRODUCT DESCRIPTION CANNOT BE GREATER THAN 500 CHARACTERS", ResultType.INVALID);
        }

        if(product.getQuantity() <= 0 && product.getProductId() == 0) {
            result.addMessages("PRODUCT QUANTITY MUST BE GREATER THAN ZERO", ResultType.INVALID);
        }

        if(product.getSaleType() == SaleType.AUCTION && product.getQuantity() != 1 && product.getProductId() == 0) {
            result.addMessages("PRODUCTS THAT ARE AUCTIONS MUST HAVE A QUANTITY OF ONE", ResultType.INVALID);
        }

        if(product.getCondition() == null) {
            result.addMessages("CONDITION IS REQUIRED", ResultType.INVALID);
        }

        if(product.getProductId() != 0) {
            if(!bidRepository.findByProduct(product).isEmpty() && product.getWinningBid() == null) {
                result.addMessages("CANNOT UPDATE PRODUCT THAT HAS ACTIVE BIDS", ResultType.INVALID);
            }
        }

        return result;
    }
}
