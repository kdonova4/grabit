package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.dto.OrderCreateDTO;
import com.kdonova4.grabit.model.dto.OrderProductResponseDTO;
import com.kdonova4.grabit.model.dto.OrderResponseDTO;
import com.kdonova4.grabit.model.entity.Address;
import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Order;

import java.util.List;

public class OrderMapper {


    public static Order fromDTO(OrderCreateDTO orderDTO, AppUser user, Address shipping, Address billing) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shipping);
        order.setBillingAddress(billing);

        return order;
    }

    public static OrderResponseDTO toResponse(Order order) {
        List<OrderProductResponseDTO> orderProductDTOs = OrderProductMapper.toDTO(order.getOrderProducts());


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
