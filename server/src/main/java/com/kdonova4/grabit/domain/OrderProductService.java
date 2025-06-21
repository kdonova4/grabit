package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.OrderProductRepository;
import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.domain.mapper.OrderMapper;
import com.kdonova4.grabit.domain.mapper.OrderProductMapper;
import com.kdonova4.grabit.enums.SaleType;
import com.kdonova4.grabit.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderProductService {
    private final OrderProductRepository repository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderProductService(OrderProductRepository repository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public List<OrderProduct> findAll() {
        return repository.findAll();
    }

    public List<OrderProduct> findByProduct(Product product) {
        return repository.findByProduct(product);
    }

    public List<OrderProduct> findByOrder(Order order) {
        return repository.findByOrder(order);
    }

    public Optional<OrderProduct> findById(int id) {
        return repository.findById(id);
    }

    public Result<OrderProductResponseDTO> create(OrderProductCreateDTO orderProductCreateDTO) {

        Product product = productRepository.findById(orderProductCreateDTO.getProductId()).orElse(null);
        Order order = orderRepository.findById(orderProductCreateDTO.getOrderId()).orElse(null);

        OrderProduct orderProduct = OrderProductMapper.toOrderProduct(orderProductCreateDTO, order, product);

        Result<OrderProductResponseDTO> result = validate(orderProduct);

        if(!result.isSuccess())
            return result;

        if(orderProduct.getOrderProductId() != 0) {
            result.addMessages("OrderProductId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        orderProduct = repository.save(orderProduct);
        result.setPayload(OrderProductMapper.toDTO(orderProduct));
        return result;
    }

    private Result<OrderProductResponseDTO> validate(OrderProduct orderProduct) {
        Result<OrderProductResponseDTO> result = new Result<>();

        if(orderProduct == null) {
            result.addMessages("ORDER PRODUCT CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(orderProduct.getProduct() == null || orderProduct.getProduct().getProductId() <= 0) {
            result.addMessages("PRODUCT IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(orderProduct.getOrder() == null || orderProduct.getOrder().getOrderId() <= 0) {
            result.addMessages("ORDER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Product> product = productRepository.findById(orderProduct.getProduct().getProductId());
        Optional<Order> order = orderRepository.findById(orderProduct.getOrder().getOrderId());

        if(product.isEmpty()) {
            result.addMessages("PRODUCT MUST EXIST", ResultType.INVALID);
        }

        if(order.isEmpty()) {
            result.addMessages("ORDER MUST EXIST", ResultType.INVALID);
        }

        if(orderProduct.getQuantity() < 1) {
            result.addMessages("QUANTITY MUST BE 1 OR GREATER", ResultType.INVALID);
        }

        if(product.get().getSaleType() == SaleType.AUCTION) {
            if(orderProduct.getUnitPrice().compareTo(product.get().getWinningBid()) != 0) {
                result.addMessages("UNIT PRICE IS NOT SET TO WINNING BID", ResultType.INVALID);
            }
        } else {
            if(orderProduct.getUnitPrice().compareTo(product.get().getPrice()) != 0) {
                result.addMessages("UNIT PRICE IS NOT EQUAL TO SALE PRICE", ResultType.INVALID);
            }
        }

        return result;
    }
}
