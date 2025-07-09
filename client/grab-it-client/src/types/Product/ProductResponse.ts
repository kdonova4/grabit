export type SaleType = 'BUY_NOW' | 'AUCTION';
export type ProductStatus = 'ACTIVE' | 'SOLD' | 'REMOVED' | 'EXPIRED' | 'HELD';
export type ConditionType = 'NEW' | 'GOOD' | 'EXCELLENT' | 'FAIR' | 'USED' | 'REFURBISHED' | 'DAMAGED';


export interface ProductResponse {
  productId: number;
  postedAt: string;
  saleType: SaleType;
  productStatus: ProductStatus;
  productName: string;
  description: string;
  price: number;
  conditionType: ConditionType;
  quantity: number;
  auctionEnd: Date | null;
  winningBid: number | null;
  offerPrice: number | null;
  userId: number;
  categoryIds: number[]
}