import { ConditionType, ProductStatus, SaleType } from "./ProductResponse";

export interface ProductFormState {
    productId: number;
    saleType: SaleType
    productName: string;
    description: string;
    price: number;
    condition: ConditionType;
    quantity: number;
    status: ProductStatus;
    winningBid: number | null;
    userId: number;
    categoryIds: number[];
}