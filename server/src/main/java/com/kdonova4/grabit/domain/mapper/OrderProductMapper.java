package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.*;

import java.util.ArrayList;
import java.util.List;

public class OrderProductMapper {

    public static List<OrderProductResponseDTO> toDTO(List<OrderProduct> orderProducts) {
        List<OrderProductResponseDTO> orderProductDTOS = new ArrayList<>();
        for(OrderProduct op : orderProducts) {
            orderProductDTOS.add(new OrderProductResponseDTO(op.getOrderProductId(), op.getOrder().getOrderId(),op.getProduct().getProductId(), op.getQuantity(), op.getUnitPrice(), op.getSubTotal()));
        }

        return orderProductDTOS;
    }

    public static OrderProductResponseDTO toDTO(OrderProduct orderProduct) {
        return new OrderProductResponseDTO(
                orderProduct.getOrderProductId(),
                orderProduct.getOrder().getOrderId(),
                orderProduct.getProduct().getProductId(),
                orderProduct.getQuantity(),
                orderProduct.getUnitPrice(),
                orderProduct.getSubTotal()
        );
    }

    public static OrderProduct toOrderProduct(OrderProductCreateDTO orderProductDTO, Order order, Product product) {
        return new OrderProduct(
                0,
                order,
                product,
                orderProductDTO.getQuantity(),
                orderProductDTO.getUnitPrice(),
                orderProductDTO.getSubTotal()
        );
    }

    public static List<OrderProductCreateDTO> toCreateDTO(List<OrderProduct> orderProducts) {
        List<OrderProductCreateDTO> orderProductDTOS = new ArrayList<>();
        for(OrderProduct op : orderProducts) {
            orderProductDTOS.add(new OrderProductCreateDTO(op.getOrder().getOrderId(), op.getProduct().getProductId(), op.getQuantity(), op.getUnitPrice(), op.getSubTotal()));
        }

        return orderProductDTOS;
    }


}
