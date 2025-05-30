package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.*;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderRepository repository;
    private final ProductRepository productRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final AddressRepository addressRepository;
    private final AppUserRepository appUserRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderProductService orderProductService;
    private final ShipmentService shipmentService;
    private final PaymentService paymentService;
    private final ShoppingCartService shoppingCartService;
    private final ProductService productService;

    public OrderService(OrderRepository repository, ProductRepository productRepository, ShoppingCartRepository shoppingCartRepository, AddressRepository addressRepository, AppUserRepository appUserRepository, OrderProductRepository orderProductRepository, OrderProductService orderProductService, ShipmentService shipmentService, PaymentService paymentService, ShoppingCartService shoppingCartService, ProductService productService) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.addressRepository = addressRepository;
        this.appUserRepository = appUserRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderProductService = orderProductService;
        this.shipmentService = shipmentService;
        this.paymentService = paymentService;
        this.shoppingCartService = shoppingCartService;
        this.productService = productService;
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    public List<Order> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    public List<Order> findByOrderStatus(OrderStatus status) {
        return repository.findByOrderStatus(status);
    }

    public List<Order> findByOrderedAtAfter(Timestamp date) {
        return repository.findByOrderedAtAfter(date);
    }

    public List<Order> findByOrderedAtBetween(Timestamp start, Timestamp end) {
        return repository.findByOrderedAtBetween(start, end);
    }

    public List<Order> findByShippingAddress(Address shipingAddress) {
        return repository.findByShippingAddress(shipingAddress);
    }

    public List<Order> findByBillingAddress(Address billingAddress) {
        return repository.findByBillingAddress(billingAddress);
    }

    public List<Order> findByTotalAmountGreaterThan(BigDecimal amount) {
        return repository.findByTotalAmountGreaterThan(amount);
    }

    public List<Order> findByTotalAmountLessThan(BigDecimal amount) {
        return repository.findByTotalAmountLessThan(amount);
    }

    public Optional<Order> findById(int id) {
        return repository.findById(id);
    }

    @Transactional
    public Result<Order> create(CheckoutRequest checkout) {
        Order order = checkout.getOrder();
        Result<Order> result = validate(order);

        if(!result.isSuccess()) {
            return result;
        }

        if(order.getOrderId() != 0) {
            result.addMessages("OrderId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        order = repository.save(order);

        Shipment shipment = checkout.getShipment();
        shipment.setOrder(order);

        Payment payment = checkout.getPayment();
        payment.setOrder(order);

        Result<Shipment> shipmentResult = shipmentService.create(shipment);
        if(!shipmentResult.isSuccess()) {
            result.addMessages(shipmentResult.getMessages().toString(), ResultType.INVALID);
            return result;
        }


        List<ShoppingCart> cartList = checkout.getCartItems();
        BigDecimal total = BigDecimal.ZERO;
        for(ShoppingCart item : cartList) {
            if(item.getProduct().getQuantity() < item.getQuantity()) {
                result.addMessages("Product " + item.getProduct().getProductName() + " is out of stock", ResultType.INVALID);
                return result;
            }

            OrderProduct op = new OrderProduct(0, order, item.getProduct(), item.getQuantity(), item.getProduct().getPrice(), null);
            Result<OrderProduct> orderProductResult = orderProductService.create(op);


            if(!orderProductResult.isSuccess()) {
                result.addMessages(orderProductResult.getMessages().toString(), ResultType.INVALID);
                return result;
            }

            total = total.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        shoppingCartService.deleteByUser(order.getUser());

        for(ShoppingCart item : cartList) {
            Product product = item.getProduct();

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productService.update(product);
        }

        order.setTotalAmount(total);
        payment.setAmountPaid(total);

        order = repository.save(order);

        Result<Payment> paymentResult = paymentService.create(payment);

        if(!paymentResult.isSuccess()) {
            result.addMessages(paymentResult.getMessages().toString(), ResultType.INVALID);
            return result;
        }

        result.setPayload(order);

        return result;
    }

    private void finalizeOrder(Order order, List<ShoppingCart> items) {

    }

    private Result<Order> validate(Order order) {
        Result<Order> result = new Result<>();

        if(order == null) {
            result.addMessages("ORDER CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(order.getUser() == null || order.getUser().getAppUserId() <= 0) {
            result.addMessages("USER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Address> billingAddress = addressRepository.findById(order.getBillingAddress().getAddressId());
        Optional<Address> shippingAddress = addressRepository.findById(order.getShippingAddress().getAddressId());
        Optional<AppUser> appUser = appUserRepository.findById(order.getUser().getAppUserId());

        if(billingAddress.isEmpty()) {
            result.addMessages("BILLING ADDRESS MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(shippingAddress.isEmpty()) {
            result.addMessages("SHIPPING ADDRESS MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(appUser.isEmpty()) {
            result.addMessages("USER MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(order.getOrderedAt() == null || order.getOrderedAt().after(Timestamp.valueOf(LocalDateTime.now()))) {
            result.addMessages("CREATED AT MUST NOT BE NULL OR IN THE FUTURE", ResultType.INVALID);
        }

        if(order.getOrderStatus() == null) {
            result.addMessages("ORDER STATUS IS REQUIRED", ResultType.INVALID);
        }

        // totalAmount must be equal to the orderProducts totals combined
        if(order.getOrderId() != 0) {
            List<OrderProduct> orderProducts = orderProductRepository.findByOrder(order);
            BigDecimal total = BigDecimal.ZERO;;
            for(OrderProduct op : orderProducts) {
                total = total.add(op.getSubTotal());
            }

            if(order.getTotalAmount().compareTo(total) != 0) {
                result.addMessages("ORDER TOTAL INVALID, DOES NOT ADD UP TO TOTAL FROM PRODUCTS IN ORDER", ResultType.INVALID);
            }
        }


        return result;
    }
}
