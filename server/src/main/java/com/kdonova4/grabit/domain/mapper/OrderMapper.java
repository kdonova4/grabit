package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.*;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {


    public static Order fromDTO(OrderCreateDTO orderDTO, AppUser user, Address shipping, Address billing) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shipping);
        order.setBillingAddress(billing);

        return order;
    }

    public static OrderResponseDTO toResponse(Order order, List<OrderProductDTO> orderProductDTOs) {
        return new OrderResponseDTO(
                order.getOrderId(),
                order.getUser().getAppUserId(),
                order.getOrderedAt(),
                order.getShippingAddress().getAddressId(),
                order.getBillingAddress().getAddressId(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                orderProductDTOs);
    }

}
