// components/ProductViewer.tsx
import React, { useState } from "react";
import { fetchProductById } from "./ProductApi";
import { ProductResponseDTO } from "./ProductResponseDTO";

const ProductViewer: React.FC = () => {
  const [productId, setProductId] = useState("");
  const [product, setProduct] = useState<ProductResponseDTO | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleFetch = async () => {
    try {
      const data = await fetchProductById(Number(productId));
      setProduct(data);
      setError(null);
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
          <p className="text-green-700 font-bold">${product.price}</p>
        </div>
      )}

      {error && <p className="text-red-600 mt-2">{error}</p>}
    </div>
  );
};

export default ProductViewer;
