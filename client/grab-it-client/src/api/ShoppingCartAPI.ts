import { ShoppingCartDTO } from "../types/ShoppingCart/ShoppingCartDTO";

export async function findCartItemsByUser(userId: number, token: string): Promise<ShoppingCartDTO[]> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/user/${userId}`, {
        method: "GET",
        headers: {
            "Constent-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });

    if (response.status === 403) {
        const data: string[] = await response.json();
        throw data;
    }

    if (!response.ok) {
        const data: string[] = await response.json();
        throw data;
    }

    const data: ShoppingCartDTO[] = await response.json();
    return data;
}

export async function findCartItemByUserAndProduct(userId: number, productId: number, token: string): Promise<ShoppingCartDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/user/${userId}/product/${productId}`, {
        method: "GET",
        headers: {
            "Constent-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });

    if (response.status === 403) {
        const data: string[] = await response.json();
        throw data;
    }

    if (!response.ok) {
        const data: string[] = await response.json();
        throw data;
    }

    const data: ShoppingCartDTO = await response.json();
    return data;
}

export async function fetchCartItemById(cartId: number): Promise<ShoppingCartDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/${cartId}`);

    if (!response.ok) {
        throw new Error(`Error finding cart item with ID ${cartId}`);
    }

    const data: ShoppingCartDTO = await response.json();
    return data;
}

export async function createCartItem(cart: ShoppingCartDTO, token: string): Promise<ShoppingCartDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/carts`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(cart)
    })

    if (response.status === 403) {
        const data: string[] = await response.json();
        throw data;
    }

    if (response.status !== 201) {
        const data: string[] = await response.json();
        throw data;
    }

    const data: ShoppingCartDTO = await response.json();
    return data;
}

export async function updateCartItem(cartId: number, cart: ShoppingCartDTO): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/${cartId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(cart),
    });

    if (response.status === 409) {
        const data: string[] = await response.json();
        throw data;
    }

    if (!response.ok) {
        const data: string[] = await response.json();
        throw data;
    }
}

export async function deleteCartItemById(cartId: number, token: string): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/${cartId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });

    if (response.status !== 204) {
        const data: string[] = await response.json();
        throw data;
    }
}