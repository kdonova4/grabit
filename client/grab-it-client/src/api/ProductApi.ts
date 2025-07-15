import { ProductCreateRequest } from "../types/Product/ProductCreateRequest";
import { ProductResponse } from "../types/Product/ProductResponse";
import { ProductSearchParams } from "../types/Product/ProductSearchParams";
import { ProductUpdateRequest } from "../types/Product/ProductUpdateRequest";

export async function fetchProductsByUser(userId: number): Promise<ProductResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/products/user/${userId}`);

    if(!response.ok) {
        throw new Error(`User ID ${userId} Not Found`);
    }

    const data: ProductResponse[] = await response.json();
    return data;
}

export async function fetchProductById(productId: number): Promise<ProductResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/products/${productId}`);

    if(!response.ok) {
        throw new Error(`Could not find Product ${productId}`)
    }

    const data: ProductResponse = await response.json();
    return data;
}

export async function fetchProductByCategory(categoryId: number): Promise<ProductResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/products/category/${categoryId}`);

    const data: ProductResponse[] = await response.json();
    return data;
}

function buildQuery(params: ProductSearchParams): string {
    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
        if(value !== undefined && value !== null) {
            query.append(key, value.toString());
        }
    });

    return query.toString();
}

export async function searchProducts(params: ProductSearchParams): Promise<ProductResponse[]> {
    const query = buildQuery(params);
    const response = await fetch(`http://localhost:8080/api/v1/products/search?${query}`);

    if(!response.ok) {
        throw new Error(`Failed to complete search: ${response.statusText}`);
    }

    const data: ProductResponse[] = await response.json();
    return data;
}

export async function createProduct(product: ProductCreateRequest, token: string): Promise<ProductResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/products`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(product)
    });

   if(response.status !== 201) {
        const errorData: string[] = await response.json();
        throw errorData
    }

    const data: ProductResponse = await response.json();
    return data;
}

export async function updateProduct(productId: number, product: ProductUpdateRequest, token: string): Promise<null | string[]> {
    const response = await fetch(`http://localhost:8080/api/v1/products/${productId}`, {
        method: "PUT",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(product),
    });

    if(response.status === 409) {
        throw new Error("Product ID conflict")
    }

    if(response.status !== 204) {
        const errors = await response.json();
        console.log(errors)
        return errors as string[];
    }

    return null;
}

export async function deleteProductById(productId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/products/${productId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}