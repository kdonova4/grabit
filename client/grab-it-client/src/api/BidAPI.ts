import { BidCreateRequest } from "../types/BidCreateRequest";
import { BidResponse } from "../types/BidResponse";

export async function fetchByUser(userId: number): Promise<BidResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/bids/user/${userId}`)

    if(!response.ok) {
        throw new Error(`User ID ${userId} Not Found`)
    }

    const data: BidResponse[] = await response.json();
    return data;
}

export async function fetchByProduct(productId: number): Promise<BidResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/bids/product/${productId}`);

    if(!response.ok) {
        throw new Error(`Product ID ${productId} Not Found`);
    }

    const data: BidResponse[] = await response.json();
    return data;
}

export async function fetchById(bidId: number): Promise<BidResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/bids/${bidId}`);

    if(!response.ok) {
        throw new Error(`Bid ID ${bidId} Not Found`)
    }

    const data: BidResponse = await response.json();
    return data;
}

export async function createBid(bid: BidCreateRequest): Promise<BidResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/bids`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(bid)
    });

    if(response.status !== 201) {
        throw new Error("Error Creating New Bid")
    }

    return await response.json();
}

export async function deleteBidById(bidId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/bids/${bidId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}