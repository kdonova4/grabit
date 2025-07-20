import { WatchlistDTO } from "../types/Watchlist/WatchlistDTO";

export async function findWatchItemsByUser(userId: number, token: string): Promise<WatchlistDTO[]> {
    const response = await fetch(`http://localhost:8080/api/v1/watchlists/user/${userId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });

    if(!response.ok) {
        throw new Error(`User ID ${userId} Not Found`)
    }

    const data: WatchlistDTO[] = await response.json();
    return data;
}

export async function findWatchItemByUserAndProduct(userId: number, productId: number): Promise<WatchlistDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/watchlists/user/${userId}/product/${productId}`);

    if(!response.ok) {
        throw new Error(`Cannot find Watch Item for User ${userId} and Product ${productId}`);
    }

    const data: WatchlistDTO = await response.json();
    return data;
}


export async function fetchWatchItemById(watchId: number): Promise<WatchlistDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/watchlists/${watchId}`);

    if(!response.ok) {
        throw new Error(`Error finding Watch Item with ID ${watchId}`);
    }

    const data: WatchlistDTO = await response.json();
    return data;
}

export async function createWatchItem(watchItem: WatchlistDTO, token: string): Promise<WatchlistDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/watchlists`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(watchItem)
    });

    if(response.status === 403) {
        throw new Error("User not authenticated")
    }

    if(response.status !== 201) {
        throw new Error("Error Creating Watch Item");
    }

    const data: WatchlistDTO = await response.json();
    return data;
}

export async function deleteWatchItemById(watchId: number, token: string): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/watchlists/${watchId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });

    if(response.status === 403) {
        throw new Error("User not authenticated")
    }

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}