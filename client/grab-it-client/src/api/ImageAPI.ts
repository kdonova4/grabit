import { ImageResponse } from "../types/ImageResponse";

export async function fetchImagesByProduct(productId: number): Promise<ImageResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/images/product/${productId}`);

    if(!response.ok) {
        throw new Error(`No Images found for Product ${productId}`)
    }

    const data: ImageResponse[] = await response.json();
    return data;
}

export async function fetchImageById(imageId: number): Promise<ImageResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/images/${imageId}`);

    if(!response.ok) {
        throw new Error(`Bid ID ${imageId} Not Found`)
    }

    const data: ImageResponse = await response.json();
    return data;
}

export async function uploadImage(productId: number, file: File): Promise<ImageResponse> {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(`http://localhost:8080/api/v1/images/${productId}/upload`, {
        method: "POST",
        body: formData
    });

    if(!response.ok) {
        throw new Error("Failed to upload image")
    }

    const data: ImageResponse = await response.json();
    return data;
}

export async function deleteBidById(imageId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/images/${imageId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}