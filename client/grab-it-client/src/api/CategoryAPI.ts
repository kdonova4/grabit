import { Category } from "../types/Category/Category";

export async function fetchCategoryByName(name: string): Promise<Category> {
    const response = await fetch(`http://localhost:8080/api/v1/categories/category-name/${name}`);

    if(response.status == 404) {
        throw new Error(`Category ${name} Not Found`)
    }

    if(!response.ok) {
        throw new Error(`Could not find ${name} category`)
    }

    const data: Category = await response.json();
    return data;
}

export async function fetchCategoryById(categoryId: number): Promise<Category> {
    const response = await fetch(`http://localhost:8080/api/v1/categories/${categoryId}`);

    if(response.status == 404) {
        throw new Error(`Category ${categoryId} Not Found`)
    }

    if(!response.ok) {
        throw new Error(`Could not find category ${categoryId}`)
    }

    const data: Category = await response.json();
    return data;
}

export async function addAddress(category: Category): Promise<Category> {
    const response = await fetch(`http://localhost:8080/api/v1/categories`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(category)
    });

    if(response.status !== 201) {
        throw new Error("Error Creating new Category");
    }

    return await response.json();
}

export async function deleteBidById(categoryId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/categories/${categoryId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}