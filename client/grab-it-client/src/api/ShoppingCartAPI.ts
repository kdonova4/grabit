import { ShoppingCartDTO } from "../types/ShoppingCart/ShoppingCartDTO";

export async function findCartItemsByUser(userId: number): Promise<ShoppingCartDTO[]> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/user/${userId}`);

    if(!response.ok) {
        throw new Error(`User ID ${userId} Not Found`)
    }

    const data: ShoppingCartDTO[] = await response.json();
    return data;
}

export async function findCartItemByUserAndProduct(userId: number, productId: number): Promise<ShoppingCartDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/user/${userId}/product/${productId}`);

    if(!response.ok) {
        throw new Error(`Cart Item not found for User ${userId} and Product ${productId}`);
    }

    const data: ShoppingCartDTO = await response.json();
    return data;
}

export async function fetchCartItemById(cartId: number): Promise<ShoppingCartDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/${cartId}`);

    if(!response.ok) {
        throw new Error(`Error finding cart item with ID ${cartId}`);
    }

    const data: ShoppingCartDTO = await response.json();
    return data;
}

export async function createCartItem(cart: ShoppingCartDTO): Promise<ShoppingCartDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/carts`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(cart)
    })

    if(response.status !== 201) {
        throw new Error("Error creating cart item")
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

    if(response.status === 409) {
        throw new Error("Cart ID conflict")
    }

    if(!response.ok) {
        throw new Error("Error updating Cart Item")
    }
}

export async function deleteCartItemById(cartId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/carts/${cartId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}