package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.*;
import com.kdonova4.grabit.domain.mapper.ReviewMapper;
import com.kdonova4.grabit.enums.ShipmentStatus;
import com.kdonova4.grabit.model.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository repository;
    private final AppUserRepository appUserRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ShipmentRepository shipmentRepository;

    public ReviewService(ReviewRepository repository, AppUserRepository appUserRepository, ProductRepository productRepository, OrderRepository orderRepository, OrderProductRepository orderProductRepository, ShipmentRepository shipmentRepository) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.shipmentRepository = shipmentRepository;
    }

    public List<Review> findAll() {
        return repository.findAll();
    }

    public List<Review> findByPostedBy(AppUser postedBy) {
        return repository.findByPostedBy(postedBy);
    }

    public List<Review> findByProduct(Product product) {
        return repository.findByProduct(product);
    }

    public List<Review> findBySeller(AppUser seller) {
        return repository.findBySeller(seller);
    }

    public List<Review> findByPostedByAndSeller(AppUser postedBy, AppUser seller) {
        return repository.findByPostedByAndSeller(postedBy, seller);
    }

    public Optional<Review> findByPostedByAndProduct(AppUser postedBy, Product product) {
        return repository.findByPostedByAndProduct(postedBy, product);
    }

    public Optional<Review> findById(int id) {
        return repository.findById(id);
    }

    public Result<ReviewResponseDTO> create(ReviewCreateDTO reviewCreateDTO) {

        AppUser postedBy = appUserRepository.findById(reviewCreateDTO.getPosterId()).orElse(null);
        AppUser seller = appUserRepository.findById(reviewCreateDTO.getSellerId()).orElse(null);
        Product product = productRepository.findById(reviewCreateDTO.getProductId()).orElse(null);

        Review review = ReviewMapper.toReview(reviewCreateDTO, postedBy, seller, product);

        Result<ReviewResponseDTO> result = validate(review);

        if(!result.isSuccess())
            return result;

        if(review.getReviewId() != 0) {
            result.addMessages("ReviewId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        review = repository.save(review);
        result.setPayload(ReviewMapper.toResponseDTO(review));
        return result;
    }

    public Result<ReviewResponseDTO> update(ReviewUpdateDTO reviewUpdateDTO) {

        Review oldReview = repository.findById(reviewUpdateDTO.getReviewId()).orElse(null);


        if(oldReview == null) {
            Result<ReviewResponseDTO> result = new Result<>();
            result.addMessages("Old Review Not Found", ResultType.NOT_FOUND);
            return result;
        }

        Review review = ReviewMapper.toReview(reviewUpdateDTO, oldReview);

        Result<ReviewResponseDTO> result = validate(review);

        if(!result.isSuccess()) {
            return result;
        }

        if(review.getReviewId() <= 0) {
            result.addMessages("REVIEW ID MUST BE SET", ResultType.INVALID);
            return result;
        }

        Optional<Review> optReview = repository.findById(review.getReviewId());
        if(optReview.isPresent()) {
            repository.save(review);
            return result;
        } else {
            result.addMessages("REVIEW " + review.getReviewId() + " NOT FOUND", ResultType.NOT_FOUND);
            return result;
        }
    }

    public boolean deleteById(int id) {
        if(repository.findById(id).isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private Result<ReviewResponseDTO> validate(Review review) {
        Result<ReviewResponseDTO> result = new Result<>();

        if(review == null) {
            result.addMessages("REVIEW CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(review.getProduct() == null || review.getProduct().getProductId() <= 0) {
            result.addMessages("PRODUCT IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(review.getPostedBy() == null || review.getPostedBy().getAppUserId() <= 0) {
            result.addMessages("POSTED BY IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(review.getSeller() == null || review.getSeller().getAppUserId() <= 0) {
            result.addMessages("SELLER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<AppUser> postedBy = appUserRepository.findById(review.getPostedBy().getAppUserId());
        Optional<AppUser> seller = appUserRepository.findById(review.getSeller().getAppUserId());
        Optional<Product> product = productRepository.findById(review.getProduct().getProductId());

        if(postedBy.isEmpty()) {
            result.addMessages("POSTED BY MUST EXIST", ResultType.INVALID);
        }

        if(seller.isEmpty()) {
            result.addMessages("SELLER MUST EXIST", ResultType.INVALID);
        }

        if(product.isEmpty()) {
            result.addMessages("PRODUCT BY MUST EXIST", ResultType.INVALID);
        }

        if(review.getRating() > 5 || review.getRating() < 1) {
            result.addMessages("RATING MUST BE BETWEEN 1 AND 5", ResultType.INVALID);
        }

        if(review.getReviewText() == null || review.getReviewText().isBlank()) {
            result.addMessages("REVIEW TEXT IS REQUIRED", ResultType.INVALID);
        } else if(review.getReviewText().length() > 500) {
            result.addMessages("REVIEW TEXT LENGTH MUST BE LESS THAN 500 CHARACTERS", ResultType.INVALID);
        }

        // Only one review is allowed per product bought from a seller AND Review is only allowed once shipment of order is delivered
        if(postedBy.isPresent() && product.isPresent()) {
            if(repository.findByPostedByAndProduct(postedBy.get(), product.get()).isPresent()) {
                result.addMessages("YOU HAVE ALREADY POSTED A REVIEW FOR THIS PRODUCT", ResultType.INVALID);
            }
            List<Order> orders = orderRepository.findByUser(postedBy.get());
            boolean hasDelivered = false;
            for(Order o : orders) {
                List<OrderProduct> orderProducts = orderProductRepository.findByOrder(o);
                for(OrderProduct op : orderProducts) {
                    if(op.getProduct().getProductId() == review.getProduct().getProductId()) {
                        Optional<Shipment> shipment = shipmentRepository.findByOrder(op.getOrder());
                        if(shipment.isPresent()) {
                            if(shipment.get().getShipmentStatus() == ShipmentStatus.DELIVERED) {
                                hasDelivered = true;
                                break;
                            }
                        }
                    }
                }
                if (hasDelivered) break;
            }

            if(!hasDelivered) {
                result.addMessages("CANNOT REVIEW PRODUCT WHEN SHIPMENT NOT YET DELIVERED", ResultType.INVALID);
            }
        }

        return result;
    }
}
