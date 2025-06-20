package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.*;
import com.kdonova4.grabit.domain.mapper.OrderMapper;
import com.kdonova4.grabit.enums.OrderStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.*;
import com.kdonova4.grabit.security.AppUserService;
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
    private final AddressService addressService;
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;
    private final OrderProductService orderProductService;
    private final ShipmentService shipmentService;
    private final PaymentService paymentService;
    private final ShoppingCartService shoppingCartService;
    private final ProductService productService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository repository, AddressRepository addressRepository, AddressService addressService, AppUserRepository appUserRepository, AppUserService appUserService, OrderProductService orderProductService, ShipmentService shipmentService, PaymentService paymentService, ShoppingCartService shoppingCartService, ProductService productService) {
        this.repository = repository;
        this.addressRepository = addressRepository;
        this.addressService = addressService;
        this.appUserRepository = appUserRepository;
        this.appUserService = appUserService;
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

    private Order setupOrder(OrderCreateDTO orderDTO) {
        AppUser user = appUserService.findUserById(orderDTO.getUserId()).orElse(null);
        Address shipping = addressService.findById(orderDTO.getShippingAddressId()).orElse(null);
        Address billing = addressService.findById(orderDTO.getBillingAddressId()).orElse(null);

        return OrderMapper.fromDTO(orderDTO, user, shipping, billing);
    }


    public Result<Order> create(Order order) {
        Result<Order> result = validate(order);

        if(!result.isSuccess())
            return result;

        if(order.getOrderId() != 0) {
            result.addMessages("OrderId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        order = repository.save(order);
        result.setPayload(order);
        return result;
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
            return result;
        }
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
