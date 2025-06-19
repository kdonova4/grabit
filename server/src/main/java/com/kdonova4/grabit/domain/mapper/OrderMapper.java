package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.model.OrderCreateDTO;

import java.util.ArrayList;

public class OrderMapper {


    public static Order fromDTO(OrderCreateDTO orderDTO, AppUser user, Address shipping, Address billing) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shipping);
        order.setBillingAddress(billing);
        order.setTotalAmount(orderDTO.getTotalAmount());

        order.setOrderProducts(new ArrayList<>());

        return order;
    }

}
