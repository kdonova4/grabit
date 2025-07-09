export type DiscountType = 'PERCENTAGE' | 'FIXED';

export interface CouponDTO {
    couponId: number;
    couponCode: string;
    discount: number;
    discountType: DiscountType;
}