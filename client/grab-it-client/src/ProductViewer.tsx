// components/ProductViewer.tsx
import React, { useState } from "react";
import { fetchProductById } from "./api/ProductApi";
import { ProductResponse } from "./types/Product/ProductResponse";
import { fetchCategoryById } from "./api/CategoryAPI";

const ProductViewer: React.FC = () => {
  const [productId, setProductId] = useState("");
  const [product, setProduct] = useState<ProductResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [categoryNames, setCategoryNames] = useState<string[]>([])

  const handleFetch = async () => {
    try {
      const data = await fetchProductById(Number(productId));
      setProduct(data);
      setError(null);

      const categoryPromises = data.categoryIds.map((id) => fetchCategoryById(id));
      const categoryResponses = await Promise.all(categoryPromises);
      const names = categoryResponses.map((category) => category.categoryName);
      setCategoryNames(names);
      
    } catch (e) {
      setError((e as Error).message);
      setProduct(null);
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-2">Fetch Product by ID</h2>
      <input
        type="number"
        value={productId}
        onChange={(e) => setProductId(e.target.value)}
        className="border px-2 py-1 mr-2"
        placeholder="Enter Product ID"
      />
      <button onClick={handleFetch} className="bg-blue-600 text-white px-3 py-1 rounded">
        Fetch
      </button>

      {product && (
        <div className="mt-4 p-4 border rounded">
          <h3 className="text-lg font-semibold">{product.productName}</h3>
          <p>{product.description}</p>
          <p>{product.saleType}</p>
          <p>{product.productStatus}</p>
          <p>{product.conditionType}</p>
          <p>{product.quantity}</p>
          <p>Category: {categoryNames.join(", ")}</p>
          <p className="text-green-700 font-bold">${product.price}</p>
        </div>
      )}

      {error && <p className="text-red-600 mt-2">{error}</p>}
    </div>
  );
};

export default ProductViewer;
