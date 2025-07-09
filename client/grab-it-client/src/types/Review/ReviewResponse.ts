export interface ReviewResponse {
    reviewId: number;
    rating: number;
    reviewText: string;
    posterId: number;
    sellerId: number;
    productId: number;
    createdAt: string;
}