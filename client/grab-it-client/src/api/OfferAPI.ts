import { OfferRequest } from "../types/OfferRequest";
import { OfferResponse } from "../types/OfferResponse";

export async function fetchOfferByUser(userId: number): Promise<OfferResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/offers/user/${userId}`)

    if(!response.ok) {
        throw new Error(`User ID ${userId} Not Found`)
    }

    const data: OfferResponse[] = await response.json();
    return data;
}

export async function fetchByProduct(productId: number): Promise<OfferResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/offers/product/${productId}`);

    if(!response.ok) {
        throw new Error(`Product ID ${productId} Not Found`);
    }

    const data: OfferResponse[] = await response.json();
    return data;
}

export async function fetchById(offerId: number): Promise<OfferResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/offers/${offerId}`);

    if(!response.ok) {
        throw new Error(`Offer ID ${offerId} Not Found`)
    }

    const data: OfferResponse = await response.json();
    return data;
}

export async function createOffer(offer: OfferRequest): Promise<OfferResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/offers`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(offer)
    });

    if(response.status !== 201) {
        throw new Error("Error creating new offer")
    }

    return await response.json();
}

export async function acceptOffer(offerId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/offers/accept/${offerId}`, {
        method: "POST"
    });

    if(!response.ok) {
        throw new Error("Error accepting offer")
    }

    
}

export async function deleteOfferById(offerId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/offers/${offerId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}