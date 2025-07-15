import { ImageResponse } from "../types/Image/ImageResponse";

export async function fetchImagesByProduct(productId: number): Promise<ImageResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/images/product/${productId}`);

    if(!response.ok) {
        console.log("ERROR HERE")
        throw new Error(`No Images found for Product ${productId}`)
    }

    const data: ImageResponse[] = await response.json();
    return data;
}

export async function fetchImageById(imageId: number): Promise<ImageResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/images/${imageId}`);

    if(!response.ok) {
        
        throw new Error(`Image ID ${imageId} Not Found`)
    }

    const data: ImageResponse = await response.json();
    return data;
}

export async function uploadImage(productId: number, files: File[], token: string): Promise<null | string[]> {
    const formData = new FormData();
    files.forEach(file => {
        formData.append("file", file);
    });
    

    const response = await fetch(`http://localhost:8080/api/v1/images/${productId}/upload`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`
        },
        body: formData
    });

    if(!response.ok) {
        const errors = await response.json();
        return errors as string[];
    }

    return null;
}

export async function deleteImageById(imageId: number, token: string): Promise<null | string[]> {
    const response = await fetch(`http://localhost:8080/api/v1/images/${imageId}`, {
        method: "DELETE",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        }
    });
    
    if(response.status !== 204) {
        const errors = await response.json();
        return errors as string[];
    }

    return null;
}