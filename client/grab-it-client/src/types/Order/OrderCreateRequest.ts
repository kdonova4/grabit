export interface OrderCreateRequest {
    userId: number;
    shippingAddressId: number;
    billingAddressId: number;
}