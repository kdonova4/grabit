import { OrderResponse } from "../types/OrderResponse";

export async function fetchOrderByUser(userId: number): Promise<OrderResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/orders/user/${userId}`)

    if(!response.ok) {
        throw new Error(`Could Not Find User ${userId}`);
    }

    const data: OrderResponse[] = await response.json();
    return data;
}

export async function fetchById(orderId: number): Promise<OrderResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/orders/${orderId}`);

    if(!response.ok) {
        throw new Error(`Order ID ${orderId} Not Found`)
    }

    const data: OrderResponse = await response.json();
    return data;
}