import { OrderProductResponse } from "./OrderProductResponse";

export type OrderStatus = 'PENDING' | 'SUCCESS' | 'FAILED';


export interface OrderResponse {
    orderId: number;
    userId: number;
    orderedAt: string;
    shippingAddressId: number;
    billingAddressId: number;
    totalAmount: number;
    status: OrderStatus;
    orderProducts: OrderProductResponse[];
}