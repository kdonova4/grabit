package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.ShoppingCart;
import com.kdonova4.grabit.model.ShoppingCartDTO;

public class ShoppingCartMapper {

    public static ShoppingCart fromDTO(ShoppingCartDTO shoppingCartDTO, AppUser user, Product product) {

        return new ShoppingCart(
                shoppingCartDTO.getShoppingCartId(),
                product, user, shoppingCartDTO.getQuantity());
    }
}
