package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.OrderProduct;
import com.kdonova4.grabit.model.OrderProductDTO;

import java.util.ArrayList;
import java.util.List;

public class OrderProductMapper {

    public static List<OrderProductDTO> toDTO(List<OrderProduct> orderProducts) {
        List<OrderProductDTO> orderProductDTOS = new ArrayList<>();
        for(OrderProduct op : orderProducts) {
            orderProductDTOS.add(new OrderProductDTO(op.getOrderProductId(), op.getProduct().getProductId(), op.getQuantity(), op.getSubTotal()));
        }

        return orderProductDTOS;
    }
}
