import { ProductResponseDTO } from "./ProductResponseDTO";

export async function fetchProductById(productId: number): Promise<ProductResponseDTO> {
  const response = await fetch(`http://localhost:8080/api/v1/products/${productId}`);

  if (!response.ok) {
    throw new Error(`Product with ID ${productId} not found`);
  }

  const data: ProductResponseDTO = await response.json();
  return data;
}