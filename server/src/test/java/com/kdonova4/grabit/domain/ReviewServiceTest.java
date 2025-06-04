package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.*;
import com.kdonova4.grabit.enums.*;
import com.kdonova4.grabit.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    AppUserRepository appUserRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderProductRepository orderProductRepository;

    @Mock
    ShipmentRepository shipmentRepository;

    @InjectMocks
    ReviewService service;

    private Review review;
    private AppUser user;
    private AppUser seller;
    private Product product;
    private Order order;
    private OrderProduct orderProduct;
    private Shipment shipment;

    @BeforeEach
    void setup() {
        user = new AppUser(1, "kdonova4", "kdonova4@gmail.com", "85c*98Kd", false, new HashSet<>());
        seller = new AppUser(2, "dono2223", "dono2223@gmail.com", "85c*98Kd", false, new HashSet<>());
        order = new Order(1, null, Timestamp.valueOf(LocalDateTime.now()), null, null, new BigDecimal(1200), OrderStatus.PENDING, new ArrayList<>());
        product = new Product(1, Timestamp.valueOf(LocalDateTime.now()), SaleType.BUY_NOW, "Electric Guitar",  "new electric guitar i just got", new BigDecimal(1200), ConditionType.EXCELLENT, 1, ProductStatus.ACTIVE, null, null, seller);
        orderProduct = new OrderProduct(1, order, product, 1, new BigDecimal(1200), new BigDecimal(1200));
        shipment = new Shipment(1, order, ShipmentStatus.DELIVERED, "TRACKTRACKTRACK123", Timestamp.valueOf(LocalDateTime.now()), null);
        review = new Review(1, 4, "Great seller, cant wait!", user, seller, product, Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    void shouldFindAll() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));

        List<Review> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(reviewRepository).findAll();
    }

    @Test
    void shouldFindByPostedBy() {
        when(reviewRepository.findByPostedBy(review.getPostedBy())).thenReturn(List.of(review));

        List<Review> actual = service.findByPostedBy(review.getPostedBy());

        assertEquals(1, actual.size());
        verify(reviewRepository).findByPostedBy(review.getPostedBy());
    }

    @Test
    void shouldFindByProduct() {
        when(reviewRepository.findByProduct(review.getProduct())).thenReturn(List.of(review));

        List<Review> actual = service.findByProduct(review.getProduct());

        assertEquals(1, actual.size());
        verify(reviewRepository).findByProduct(review.getProduct());
    }

    @Test
    void shouldFindBySeller() {
        when(reviewRepository.findBySeller(review.getSeller())).thenReturn(List.of(review));

        List<Review> actual = service.findBySeller(review.getSeller());

        assertEquals(1, actual.size());
        verify(reviewRepository).findBySeller(review.getSeller());
    }

    @Test
    void shouldFindById() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));

        Optional<Review> actual = service.findById(review.getReviewId());

        assertTrue(actual.isPresent());
        verify(reviewRepository).findById(review.getReviewId());
    }

    @Test
    void shouldCreateValid() {
        Review mockOut = review;
        review.setReviewId(0);

        when(reviewRepository.save(review)).thenReturn(mockOut);
        when(appUserRepository.findById(review.getPostedBy().getAppUserId())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(review.getSeller().getAppUserId())).thenReturn(Optional.of(seller));
        when(productRepository.findById(review.getProduct().getProductId())).thenReturn(Optional.of(product));
        when(reviewRepository.findByPostedByAndProduct(user, product)).thenReturn(Optional.empty());
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));
        when(orderProductRepository.findByOrder(order)).thenReturn(List.of(orderProduct));
        when(shipmentRepository.findByOrder(order)).thenReturn(Optional.of(shipment));

        Result<Review> actual = service.create(review);

        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldUpdateValid() {
        review.setReviewText("Nevermind this guy STINKS!!!!");
        review.setRating(1);


        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(appUserRepository.findById(review.getPostedBy().getAppUserId())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(review.getSeller().getAppUserId())).thenReturn(Optional.of(seller));
        when(productRepository.findById(review.getProduct().getProductId())).thenReturn(Optional.of(product));
        when(reviewRepository.findByPostedByAndProduct(user, product)).thenReturn(Optional.empty());
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));
        when(orderProductRepository.findByOrder(order)).thenReturn(List.of(orderProduct));
        when(shipmentRepository.findByOrder(order)).thenReturn(Optional.of(shipment));

        Result<Review> actual = service.update(review);

        assertEquals(ResultType.SUCCESS, actual.getType());

    }

    @Test
    void shouldNotUpdateMissing() {
        review.setReviewText("Nevermind this guy STINKS!!!!");
        review.setRating(1);



        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.empty());
        when(appUserRepository.findById(review.getPostedBy().getAppUserId())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(review.getSeller().getAppUserId())).thenReturn(Optional.of(seller));
        when(productRepository.findById(review.getProduct().getProductId())).thenReturn(Optional.of(product));
        when(reviewRepository.findByPostedByAndProduct(user, product)).thenReturn(Optional.empty());
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));
        when(orderProductRepository.findByOrder(order)).thenReturn(List.of(orderProduct));
        when(shipmentRepository.findByOrder(order)).thenReturn(Optional.of(shipment));

        Result<Review> actual = service.update(review);

        assertEquals(ResultType.NOT_FOUND, actual.getType());
    }

    @Test
    void shouldNotCreateInvalid() {

        when(appUserRepository.findById(review.getPostedBy().getAppUserId())).thenReturn(Optional.of(user));
        when(appUserRepository.findById(review.getSeller().getAppUserId())).thenReturn(Optional.of(seller));
        when(productRepository.findById(review.getProduct().getProductId())).thenReturn(Optional.of(product));

        Result<Review> actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setReviewId(0);
        review.setProduct(null);
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setProduct(product);
        review.setPostedBy(null);
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setPostedBy(user);
        review.setSeller(null);
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setSeller(seller);
        review.setRating(10);
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setRating(4);
        review.setReviewText(null);
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setReviewText("Test");
        review.setCreatedAt(null);
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        when(reviewRepository.findByPostedByAndProduct(user, product)).thenReturn(Optional.of(review));
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        when(reviewRepository.findByPostedByAndProduct(user, product)).thenReturn(Optional.empty());
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));
        when(orderProductRepository.findByOrder(order)).thenReturn(List.of(orderProduct));
        when(shipmentRepository.findByOrder(order)).thenReturn(Optional.of(shipment));
        shipment.setShipmentStatus(ShipmentStatus.PENDING);
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());

        shipment.setShipmentStatus(ShipmentStatus.DELIVERED);
        review = null;
        actual = service.create(review);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldDeleteById() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).deleteById(review.getReviewId());
        assertTrue(service.deleteById(review.getReviewId()));
        verify(reviewRepository).deleteById(review.getReviewId());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(reviewRepository.findById(999)).thenReturn(Optional.empty());
        assertFalse(service.deleteById(999));
        verify(reviewRepository, never()).deleteById(anyInt());
    }
}
