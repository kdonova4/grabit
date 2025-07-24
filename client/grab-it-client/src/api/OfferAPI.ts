import { OfferRequest } from "../types/Offer/OfferRequest";
import { OfferResponse } from "../types/Offer/OfferResponse";

export async function fetchOfferByUser(userId: number, token: string): Promise<OfferResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/offers/user/${userId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    })

    if(!response.ok) {
        const errorData: string[] = await response.json();
        throw errorData;
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

export async function createOffer(offer: OfferRequest, token: string): Promise<OfferResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/offers`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(offer)
    });
    

    if(response.status !== 201) {
        const errorData: string[] = await response.json();
        throw errorData;
    }

    return await response.json();
}

export async function acceptOffer(offerId: number, token: string): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/offers/accept/${offerId}`, {
        method: "POST"
    });

    if(!response.ok) {
        throw new Error("Error accepting offer")
    }

    
}

export async function deleteOfferById(offerId: number, token: string): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/offers/${offerId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });

    if(response.status !== 204) {
        const errorData: string[] = await response.json();
        throw errorData;
    }
}