package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.domain.mapper.*;
import com.kdonova4.grabit.enums.DiscountType;
import com.kdonova4.grabit.enums.ProductStatus;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.dto.*;
import com.kdonova4.grabit.model.entity.*;
import com.kdonova4.grabit.model.event.OrderPlacedEvent;
import com.kdonova4.grabit.model.event.ShipmentPlacedEvent;
import com.kdonova4.grabit.security.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final CouponService couponService;
    private final BidService bidService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public CheckoutService(OrderService orderService, AppUserService appUserService, PaymentService paymentService, ShipmentService shipmentService, AddressService addressService, OrderProductService orderProductService, ShoppingCartService shoppingCartService, ProductService productService, CouponService couponService, BidService bidService) {
        this.orderService = orderService;
        this.appUserService = appUserService;
        this.paymentService = paymentService;
        this.shipmentService = shipmentService;
        this.addressService = addressService;
        this.orderProductService = orderProductService;
        this.shoppingCartService = shoppingCartService;
        this.productService = productService;
        this.couponService = couponService;
        this.bidService = bidService;
    }

    @Transactional
    public CheckoutResponseDTO checkout(CheckoutRequestDTO checkoutRequestDTO) {


        // create order with OrderCreateDTO
        Order order = getOrder(checkoutRequestDTO.getOrderDTO());


        // create ShoppingCart List from List of ShoppingCartDTOs
        // create the list of OrderProducts from cartList from each ShoppingCartDTO

        List<ShoppingCart> shoppingCarts = createCart(checkoutRequestDTO.getCartList());
        List<OrderProduct> orderProducts = createOrderProducts(shoppingCarts, order);


        // set the orderProducts list of Order
        // store the total of all orderProducts and set the total for order
        // call orderService create method to create order
        // assign each orderProduct ot the saved order and then call update method and save the order that now had the orderproducts
        boolean stockResult = validateStock(shoppingCarts);
        if(!stockResult) {
            throw new CheckoutException("Failed to Checkout: Insufficient Stock");
        }

        if(checkoutRequestDTO.getCouponDTO() != null) {
            BigDecimal oldTotal = order.getTotalAmount();
            Optional<Coupon> coupon = couponService.findByCouponCode(checkoutRequestDTO.getCouponDTO().getCouponCode());
            if(coupon.isEmpty())
                throw new CheckoutException("Failed to Checkout: Coupon Not Found");

            BigDecimal discountPercent = BigDecimal.valueOf(coupon.get().getDiscount())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal amountSaved = oldTotal.multiply(discountPercent);
            BigDecimal newTotal = oldTotal.subtract(amountSaved);
            order.setTotalAmount(newTotal);
        }

        Result<Order> orderResult = orderService.create(order);

        if(!orderResult.isSuccess()) {
            throw new CheckoutException("Failed to create order: " + String.join(", ", orderResult.getMessages()));
        }

        Order completeOrder = orderResult.getPayload();

        List<OrderProduct> finalOrderProducts = finalizeOrderProducts(orderProducts, completeOrder);
        completeOrder.setOrderProducts(finalOrderProducts);


        for(OrderProduct op : finalOrderProducts) {
            Result<OrderProductResponseDTO> orderProductResult = orderProductService.create(op);
            if(!orderProductResult.isSuccess()) {
                throw new CheckoutException("Failed to create OrderProduct(s): " + String.join(", ", orderProductResult.getMessages()));
            }
        }


        // create shipment
        // assign order
        // generate tracking number and everything needed
        // call shipmentService create method

        Shipment shipment = new Shipment();
        shipment.setShipmentId(0);
        shipment.setOrder(completeOrder);


        Result<ShipmentResponseDTO> shipmentResult = shipmentService.create(shipment);



        if(!shipmentResult.isSuccess()) {
            throw new CheckoutException("Failed to create Shipment: " + String.join(", ", shipmentResult.getMessages()));
        }



        // create payment
        // assign order
        // assign amount paid (total)
        // generate timestamp
        // call paymentService create method

        Payment payment = new Payment();
        payment.setAmountPaid(completeOrder.getTotalAmount());
        payment.setOrder(completeOrder);

        Result<PaymentResponseDTO> paymentResult = paymentService.create(payment);

        if(!paymentResult.isSuccess()) {
            throw new CheckoutException("Failed to create Payment: " + String.join(", ", paymentResult.getMessages()));
        }

        // delete shopping cart by user
        // for every shopping cart item, subtract the item quantity from the product quantity
        // then call the productService update method


        for(ShoppingCart item : shoppingCarts) {
            Product product = item.getProduct();

            product.setQuantity(product.getQuantity() - item.getQuantity());
            if(product.getQuantity() == 0)
            {
                product.setProductStatus(ProductStatus.SOLD);
            }


            Result<ProductResponseDTO> productResult = productService.update(ProductMapper.toUpdateDTO(product));
            if(!productResult.isSuccess()) {
                throw new CheckoutException("Failed to update Product: " + String.join(", ", productResult.getMessages()));
            }
        }




        shoppingCartService.deleteByUser(completeOrder.getUser());

        // publish the event for shipment
        // publish the event for order
        eventPublisher.publishEvent(new OrderPlacedEvent(completeOrder.getOrderId()));
        eventPublisher.publishEvent(new ShipmentPlacedEvent(shipmentResult.getPayload().getShipmentId()));


        return createCheckoutResponse(completeOrder, ShipmentMapper.toShipment(shipmentResult.getPayload(), completeOrder), PaymentMapper.toPayment(paymentResult.getPayload(), completeOrder));
    }

    private CheckoutResponseDTO createCheckoutResponse(Order order, Shipment shipment, Payment payment) {
        Order actualOrder = orderService.findById(order.getOrderId()).orElseThrow(() -> new EntityNotFoundException("Order " + order.getOrderId() + " Not Found"));
        Shipment actualShipment = shipmentService.findById(shipment.getShipmentId()).orElseThrow(() -> new EntityNotFoundException("Shipment " + order.getOrderId() + " Not Found"));
        Payment actualPayment = paymentService.findById(payment.getPaymentId()).orElseThrow(() -> new EntityNotFoundException("Payment " + order.getOrderId() + " Not Found"));
        List<OrderProductResponseDTO> orderProducts = OrderProductMapper.toDTO(actualOrder.getOrderProducts());

        OrderResponseDTO orderResponseDTO = OrderMapper.toResponse(actualOrder);
        ShipmentResponseDTO shipmentResponseDTO = ShipmentMapper.toResponse(actualShipment);
        PaymentResponseDTO paymentResponseDTO = PaymentMapper.toResponse(actualPayment);

        return new CheckoutResponseDTO(orderResponseDTO, shipmentResponseDTO, paymentResponseDTO);
    }

    // validate stock
    private boolean validateStock(List<ShoppingCart> cartList) {
        for(ShoppingCart items : cartList) {
            if(items.getProduct().getQuantity() < items.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    private Order getOrder(OrderCreateDTO orderCreateDTO) {
        AppUser user = appUserService.findUserById(orderCreateDTO.getUserId()).orElse(null);
        Address shipping = addressService.findById(orderCreateDTO.getShippingAddressId()).orElse(null);
        Address billing = addressService.findById(orderCreateDTO.getBillingAddressId()).orElse(null);

        return OrderMapper.fromDTO(orderCreateDTO, user, shipping, billing);
    }

    private List<ShoppingCart> createCart(List<ShoppingCartDTO> cartDTOS) {
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        for(ShoppingCartDTO shoppingCartDTO : cartDTOS) {
            AppUser user = appUserService.findUserById(shoppingCartDTO.getUserId()).orElseThrow(() -> new EntityNotFoundException("User " + shoppingCartDTO.getUserId() + " Not Found"));
            Product product = productService.findById(shoppingCartDTO.getProductId()).orElseThrow(() -> new EntityNotFoundException("User " + shoppingCartDTO.getProductId() + " Not Found"));

            shoppingCarts.add(ShoppingCartMapper.fromDTO(shoppingCartDTO, user, product));
        }

        return shoppingCarts;
    }

    private List<OrderProduct> createOrderProducts(List<ShoppingCart> shoppingCarts, Order order) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderProduct> orderProducts = new ArrayList<>();

        for(ShoppingCart item : shoppingCarts) {


            if(item.getProduct().getSaleType() == SaleType.AUCTION) {
                orderProducts.add(new OrderProduct(0, null, item.getProduct(), item.getQuantity(), item.getProduct().getWinningBid(), item.getProduct().getWinningBid()));
                total = total.add(item.getProduct().getWinningBid());
            } else {
                BigDecimal unitPrice = BigDecimal.ZERO;
                if(item.getProduct().getProductStatus() == ProductStatus.HELD) {
                    unitPrice = item.getProduct().getOfferPrice();
                } else {
                    unitPrice = item.getProduct().getPrice();
                }

                BigDecimal subTotal = new BigDecimal(item.getQuantity()).multiply(unitPrice);

                orderProducts.add(new OrderProduct(0, null, item.getProduct(), item.getQuantity(), unitPrice, subTotal));
                total = total.add(subTotal);
            }
        }


        order.setTotalAmount(total);
        return orderProducts;
    }

    private List<OrderProduct> finalizeOrderProducts(List<OrderProduct> orderProducts, Order order) {
        for(OrderProduct op : orderProducts) {
            op.setOrder(order);
        }

        return orderProducts;
    }


    @Transactional
    @Scheduled(fixedRate = 60000)
    public void expireAuctionProduct() {
        System.out.println("RUNNING EXPIRE");
        List<Product> expiredAuctions = productService.findBySaleTypeAndProductStatusAndAuctionEndBefore(SaleType.AUCTION, ProductStatus.ACTIVE, LocalDateTime.now());
        expiredAuctions.forEach(auction -> {
            List<Bid> bids = bidService.findByProductOrderByBidAmountDesc(auction);
            if(bids.isEmpty()) {
                auction.setProductStatus(ProductStatus.EXPIRED);
                productService.update(ProductMapper.toUpdateDTO(auction));
            } else {
                completeAuction(bids);
                System.out.println("COMPLETING AUCTION ORDER");
            }

        });
    }


    private void completeAuction(List<Bid> bids) {
        bids.get(0).getProduct().setWinningBid(bids.get(0).getBidAmount());
        productService.update(ProductMapper.toUpdateDTO(bids.get(0).getProduct()));
        Result<ShoppingCartDTO> shoppingCartDTO = shoppingCartService.create(
                new ShoppingCartDTO(0, bids.get(0).getProduct().getProductId(), bids.get(0).getUser().getAppUserId(), 1)
        );

        List<Address> addresses = addressService.findByUser(bids.get(0).getUser());
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(
                bids.get(0).getUser().getAppUserId(),
                addressService.findByUser(bids.get(0).getUser()).get(0).getAddressId(),
                addressService.findByUser(bids.get(0).getUser()).get(0).getAddressId()
        );

        CheckoutRequestDTO checkoutRequestDTO = new CheckoutRequestDTO(
                orderCreateDTO,
                List.of(shoppingCartDTO.getPayload()),
                null
        );

        checkout(checkoutRequestDTO);
    }
}
