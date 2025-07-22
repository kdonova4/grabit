import { useAuth } from "../AuthContext";
import { BidCreateRequest } from "../types/Bid/BidCreateRequest";
import { BidResponse } from "../types/Bid/BidResponse";


export async function fetchByUser(userId: number, token: string): Promise<BidResponse[]> {


    const response = await fetch(`http://localhost:8080/api/v1/bids/user/${userId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",

        }
    })

    if(response.status === 403) {
        throw new Error("User Not Authenticated")
    }

    if (response.status === 404) {
        throw new Error(`User ID ${userId} Not Found`)
    }

    const data: BidResponse[] = await response.json();
    return data;
}

export async function fetchByProduct(productId: number): Promise<BidResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/bids/product/${productId}`);

    if (!response.ok) {
        throw new Error(`Product ID ${productId} Not Found`);
    }

    const data: BidResponse[] = await response.json();
    return data;
}

export async function fetchById(bidId: number): Promise<BidResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/bids/${bidId}`);

    if (!response.ok) {
        throw new Error(`Bid ID ${bidId} Not Found`)
    }

    const data: BidResponse = await response.json();
    return data;
}

export async function createBid(bid: BidCreateRequest, token: string): Promise<BidResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/bids`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(bid)
    });

    if (response.status !== 201) {
        const errorData: string[] = await response.json();
        throw errorData;
    }

    return await response.json();
}

export async function deleteBidById(bidId: number, token: string): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/bids/${bidId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });

    if (response.status !== 204) {
        const errorData: string[] = await response.json();
        throw errorData;
    }
}