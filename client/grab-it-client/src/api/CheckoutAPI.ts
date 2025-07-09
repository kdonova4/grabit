import { CheckoutRequest } from "../types/Checkout/CheckoutRequest";
import { CheckoutResponse } from "../types/Checkout/CheckoutResponse";

export async function checkout(checkout: CheckoutRequest): Promise<CheckoutResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/checkout`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(checkout)
    });

    const data: CheckoutResponse = await response.json();
    return data;
}