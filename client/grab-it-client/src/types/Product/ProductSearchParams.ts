import { ConditionType, ProductResponse, ProductStatus, SaleType } from "./ProductResponse";

export interface ProductSearchParams {
    productName?: string;
    minPrice?: number;
    maxPrice: number;
    status?: ProductStatus;
    condition?: ConditionType;
    saleType?: SaleType;
    categoryId: number;
}