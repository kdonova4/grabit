package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.OrderProduct;
import com.kdonova4.grabit.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer> {

    List<OrderProduct> findByOrder(Order order);

    List<OrderProduct> findByProduct(Product product);
}
