package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.model.CheckoutRequestDTO;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.security.AppUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {

    private final OrderService orderService;
    private final AppUserService appUserService;
    private final PaymentService paymentService;
    private final ShipmentService shipmentService;
    private final AddressService addressService;
    private final OrderProductService orderProductService;
    private final ShoppingCartService shoppingCartService;
    private final ProductService productService;

    public CheckoutService(OrderService orderService, AppUserService appUserService, PaymentService paymentService, ShipmentService shipmentService, AddressService addressService, OrderProductService orderProductService, ShoppingCartService shoppingCartService, ProductService productService) {
        this.orderService = orderService;
        this.appUserService = appUserService;
        this.paymentService = paymentService;
        this.shipmentService = shipmentService;
        this.addressService = addressService;
        this.orderProductService = orderProductService;
        this.shoppingCartService = shoppingCartService;
        this.productService = productService;
    }

    @Transactional
    public Result<CheckoutResponseDTO> checkout(CheckoutRequestDTO checkoutRequestDTO) {

        // create order with OrderCreateDTO

        // create ShoppingCart List from List of ShoppingCartDTOs
        // create the list of OrderProducts from cartList from each ShoppingCartDTO
        //



        // set the orderProducts list of Order
        // store the total of all orderProducts and set the total for order
        // call orderService create method to create order
        // assign each orderProduct ot the saved order and then call update method and save the order that now had the orderproducts


        // create shipment
        // assign order
        // generate tracking number and everything needed
        // call shipmentService create method

        // create payment
        // assign order
        // assign amount paid (total)
        // generate timestamp
        // call paymentService create method

        // delete shopping cart by user

        // for every shopping cart item, subtract the item quantity from the product quantity
        // then call the productService update method

        // publish the event for shipment
        // publish the event for order


        return null;
    }

    // validate stock

    // validateAmounts

}
