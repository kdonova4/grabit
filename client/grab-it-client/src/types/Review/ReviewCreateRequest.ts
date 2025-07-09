export interface ReviewCreateRequest {
    rating: number;
    reviewText: string;
    posterId: number;
    sellerId: number;
    productId: number;
}