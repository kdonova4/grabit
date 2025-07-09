import { CouponDTO } from "../Coupon/CouponDTO";
import { OrderCreateRequest } from "../Order/OrderCreateRequest";
import { ShoppingCartDTO } from "../ShoppingCart/ShoppingCartDTO";

export interface CheckoutRequest {
    orderDTO: OrderCreateRequest;
    cartList: ShoppingCartDTO[];
    couponDTO: CouponDTO;
}