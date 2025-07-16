import { ConditionType, SaleType } from "./ProductResponse";

export interface ProductCreateRequest {
    saleType: SaleType;
    productName: string;
    description: string;
    price: number;
    conditionType: ConditionType;
    quantity: number;
    userId: number;
    categoryId: number;
}