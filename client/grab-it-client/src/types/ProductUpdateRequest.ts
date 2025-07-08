import { ConditionType, ProductStatus } from "./ProductResponse";

export interface ProductUpdateRequest {
    productId: number;
    productName: string;
    description: string;
    price: number;
    condition: ConditionType;
    quantity: number;
    status: ProductStatus;
    winningBid: number;
    categoryIds: number[];
}