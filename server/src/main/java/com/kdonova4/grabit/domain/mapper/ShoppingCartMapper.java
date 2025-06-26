package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.entity.AppUser;
import com.kdonova4.grabit.model.entity.Product;
import com.kdonova4.grabit.model.entity.ShoppingCart;
import com.kdonova4.grabit.model.dto.ShoppingCartDTO;

public class ShoppingCartMapper {

    public static ShoppingCart fromDTO(ShoppingCartDTO shoppingCartDTO, AppUser user, Product product) {

        return new ShoppingCart(
                shoppingCartDTO.getShoppingCartId(),
                product, user, shoppingCartDTO.getQuantity());
    }

    public static ShoppingCartDTO toResponseDTO(ShoppingCart shoppingCart) {
        return new ShoppingCartDTO(
                shoppingCart.getShoppingCartId(),
                shoppingCart.getProduct().getProductId(),
                shoppingCart.getUser().getAppUserId(),
                shoppingCart.getQuantity()
        );
    }

    public static ShoppingCart fromDTOUpdate(ShoppingCartDTO cartDTO, ShoppingCart cart) {
        return new ShoppingCart(
                cartDTO.getShoppingCartId(),
                cart.getProduct(),
                cart.getUser(),
                cartDTO.getQuantity()
        );
    }
}
