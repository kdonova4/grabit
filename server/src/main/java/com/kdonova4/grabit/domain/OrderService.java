package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.*;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final AddressRepository addressRepository;
    private final AppUserRepository appUserRepository;
    private final OrderProductService orderProductService;
    private final ShipmentService shipmentService;
    private final PaymentService paymentService;
    private final ShoppingCartService shoppingCartService;
    private final ProductService productService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository repository, AddressRepository addressRepository, AppUserRepository appUserRepository, OrderProductService orderProductService, ShipmentService shipmentService, PaymentService paymentService, ShoppingCartService shoppingCartService, ProductService productService) {
        this.repository = repository;
        this.addressRepository = addressRepository;
        this.appUserRepository = appUserRepository;
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

        // setup shipment
        Shipment shipment = checkout.getShipment();
        Payment payment = checkout.getPayment();
        List<ShoppingCart> cartList = checkout.getCartItems();

        shipment.setOrder(order);
        payment.setOrder(order);

        List<OrderProduct> orderProducts = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        Result<Order> stockResult = validateStock(cartList);
        if(!stockResult.isSuccess())
            return stockResult;

        total = setupOrderProducts(cartList, order, orderProducts, total);

        order.setTotalAmount(total);
        payment.setAmountPaid(total);

        result = validate(order);
        Result<Order> totalResult = validateAmounts(order, orderProducts);
        if(!result.isSuccess() || !totalResult.isSuccess()) {
            result.addMessages(totalResult.getMessages().toString(), ResultType.INVALID);
            return result;
        }

        order = repository.save(order);


        Result<Order> finalizeResult = finalizeOrder(order, shipment, payment, orderProducts, cartList);



        if (!finalizeResult.isSuccess())
            return finalizeResult;

        eventPublisher.publishEvent(new OrderPlacedEvent(order.getOrderId()));

        result.setPayload(order);
        return result;
    }

    private static BigDecimal setupOrderProducts(List<ShoppingCart> cartList, Order order, List<OrderProduct> orderProducts, BigDecimal total) {
        for(ShoppingCart item : cartList) {
            OrderProduct op = getOrderProduct(item, order);
            orderProducts.add(op);


            total = total.add(op.getUnitPrice().multiply(BigDecimal.valueOf(op.getQuantity())));
        }
        return total;
    }

    public Result<Order> update(Order order) {
        Result<Order> result = validate(order);

        if(!result.isSuccess()) {
            return result;
        }

        if(order.getOrderId() <= 0) {
            result.addMessages("ORDER ID MUST BE SET", ResultType.INVALID);
            return result;
        }

        Optional<Order> oldOrder = repository.findById(order.getOrderId());
        if(oldOrder.isPresent()) {
            repository.save(order);
            return result;
        } else {
            result.addMessages("ORDER " + order.getOrderId() + " NOT FOUND", ResultType.NOT_FOUND);
            return result;        }
    }

    private static OrderProduct getOrderProduct(ShoppingCart item, Order order) {
        BigDecimal unitPrice = item.getProduct().getPrice();
        BigDecimal subTotal = new BigDecimal(item.getQuantity()).multiply(unitPrice);

        OrderProduct op;
        if(item.getProduct().getSaleType() == SaleType.BUY_NOW) {
            op = new OrderProduct(0, order, item.getProduct(), item.getQuantity(), item.getProduct().getPrice(), subTotal);
        } else {
            op = new OrderProduct(0, order, item.getProduct(), item.getQuantity(), item.getProduct().getWinningBid(), item.getProduct().getWinningBid());
        }

        op.setOrder(order);
        return op;
    }

    private Result<Order> finalizeOrder(Order order, Shipment shipment, Payment payment, List<OrderProduct> orderProducts, List<ShoppingCart> cartList) {
        Result<Order> result = new Result<>();

        Result<Shipment> shipmentResult = shipmentService.create(shipment);
        if(!shipmentResult.isSuccess()) {
            result.addMessages(shipmentResult.getMessages().toString(), ResultType.INVALID);
            return result;
        }

        Result<Payment> paymentResult = paymentService.create(payment);

        if(!paymentResult.isSuccess()) {
            result.addMessages(paymentResult.getMessages().toString(), ResultType.INVALID);
            return result;
        }

        for(OrderProduct op : orderProducts) {
            Result<OrderProduct> orderProductResult = orderProductService.create(op);
            if(!orderProductResult.isSuccess()) {
                result.addMessages(orderProductResult.getMessages().toString(), ResultType.INVALID);
                return result;
            }
        }

        shoppingCartService.deleteByUser(order.getUser());

        for(ShoppingCart item : cartList) {
            Product product = item.getProduct();

            product.setQuantity(product.getQuantity() - item.getQuantity());
            Result<Product> productResult = productService.update(product);
            if(!productResult.isSuccess()) {
                result.addMessages(productResult.getMessages().toString(), ResultType.INVALID);
                return result;
            }
        }

        eventPublisher.publishEvent(new ShipmentPlacedEvent(shipmentResult.getPayload().getShipmentId()));
        return result;
    }
    
    private Result<Order> validateStock(List<ShoppingCart> cartList) {
        Result<Order> result = new Result<>();
        for(ShoppingCart item : cartList) {
            if(item.getProduct().getQuantity() < item.getQuantity()) {
                result.addMessages("Product " + item.getProduct().getProductName() + " is out of stock", ResultType.INVALID);
                return result;
            }
        }
        
        return result;
    }

    Result<Order> validate(Order order) {
        Result<Order> result = new Result<>();

        if(order == null) {
            result.addMessages("ORDER CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(order.getUser() == null || order.getUser().getAppUserId() <= 0) {
            result.addMessages("USER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(order.getShippingAddress() == null || order.getShippingAddress().getAddressId() <= 0) {
            result.addMessages("SHIPPING ADDRESS IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(order.getBillingAddress() == null || order.getBillingAddress().getAddressId() <= 0) {
            result.addMessages("BILLING ADDRESS IS REQUIRED", ResultType.INVALID);
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

        }


        return result;
    }


    Result<Order> validateAmounts(Order order, List<OrderProduct> orderProducts) {
        Result<Order> result = new Result<>();
        BigDecimal total = BigDecimal.ZERO;
        for(OrderProduct op : orderProducts) {
            total = total.add(op.getSubTotal());
        }

        if(order.getTotalAmount().compareTo(total) != 0) {
            result.addMessages("ORDER TOTAL INVALID, DOES NOT ADD UP TO TOTAL FROM PRODUCTS IN ORDER", ResultType.INVALID);
        }

        return result;
    }
}
