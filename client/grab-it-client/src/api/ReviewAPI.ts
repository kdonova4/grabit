import { ReviewCreateRequest } from "../types/Review/ReviewCreateRequest";
import { ReviewResponse } from "../types/Review/ReviewResponse";
import { ReviewUpdateRequest } from "../types/Review/ReviewUpdateRequest";

export async function fetchReviewsByUser(userId: number): Promise<ReviewResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews/user/${userId}`);

    if(!response.ok) {
        throw new Error(`User ID ${userId} Not Found`)
    }

    const data: ReviewResponse[] = await response.json();
    return data;
}

export async function fetchReviewsByProduct(productId: number): Promise<ReviewResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews/product/${productId}`);

    if(!response.ok) {
        throw new Error(`Product ID ${productId} Not Found`);
    }

    const data: ReviewResponse[] = await response.json();
    return data;
}

export async function fetchReviewsBySeller(sellerId: number): Promise<ReviewResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews/product/${sellerId}`);

    if(!response.ok) {
        throw new Error(`Seller ID ${sellerId} Not Found`);
    }

    const data: ReviewResponse[] = await response.json();
    return data;
}

export async function FindByPostedByAndSeller(postedBy: number, sellerId: number): Promise<ReviewResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews/user/${postedBy}/seller/${sellerId}`);

    if(!response.ok) {
        throw new Error(`Reviews Not Found for Seller and Poster`);
    }

    const data: ReviewResponse[] = await response.json();
    return data;
}

export async function FindByPostedByAndProduct(postedBy: number, productId: number): Promise<ReviewResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews/user/${postedBy}/product/${productId}`);

    if(!response.ok) {
        throw new Error(`Review Not Found for Poster and Product`);
    }

    const data: ReviewResponse = await response.json();
    return data;
}

export async function fetchById(reviewId: number): Promise<ReviewResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews/${reviewId}`);

    if(!response.ok) {
        throw new Error(`Review ID ${reviewId} Not Found`)
    }

    const data: ReviewResponse = await response.json();
    return data;
}

export async function createReview(review: ReviewCreateRequest): Promise<ReviewResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(review)
    });

    if(response.status !== 201) {
        throw new Error(`There was a problem creating the review`)
    }

    const data: ReviewResponse = await response.json();
    return data;
}

export async function updateReview(reviewId: number, review: ReviewUpdateRequest): Promise<ReviewResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews/${reviewId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(review)
    });

    if(response.status === 409) {
        throw new Error("Review ID Conflict");
    }

    if(!response.ok) {
        throw new Error("Error Updating Review")
    }

    const data: ReviewResponse = await response.json();
    return data;
}


export async function deleteReviewById(reviewId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/reviews/${reviewId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}