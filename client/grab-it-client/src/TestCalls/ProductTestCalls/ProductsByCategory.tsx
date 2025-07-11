import { useState } from "react"
import { fetchProductByCategory } from "../../api/ProductApi"
import { ProductResponse } from "../../types/Product/ProductResponse";



const ProductByCategory: React.FC = () => {
    const [categoryId, setCategoryId] = useState("");
    const [products, setProducts] = useState<ProductResponse[] | null>(null);
    const [error, setError] = useState<string | null>(null);


    const handleFetch = async () => {
        try{
            const data = await fetchProductByCategory(Number(categoryId));
            setProducts(data);
            setError(null);
        } catch (e) {
            setError((e as Error).message);
            setProducts(null);
        }
        
    }


    return (
        <div>
            <h2>Fetch Products By Category Id</h2>
            <input
                type="number"
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                className="border px-2 py-1 mr-2"
                placeholder="Enter Category ID"
            />
            <button onClick={handleFetch} className="bg-blue-600 text-white px-3 py-1 rounded">
                Fetch Products
            </button>

            <table className="table table-stripped">
                    <thead className="thead-dark">
                        <tr>
                            <th>Product ID</th>
                            <th>Posted At</th>
                            <th>Sale Type</th>
                            <th>Product Status</th>
                            <th>Product Name</th>
                            <th>Description</th>
                            <th>price</th>
                            <th>Condition</th>
                            <th>quantity</th>
                            <th>Auction End</th>
                            <th>Winning Bid</th>
                            <th>Offer Price</th>
                            <th>User ID</th>
                            <th>Categories</th>
                            <th>&nbsp;</th>
                        </tr>
                    </thead>
                    <tbody>

                        {products?.map(product => (
                            <tr key={product.productId}>
                                <td>{product.productId}</td>
                                <td>{product.postedAt}</td>
                                <td>{product.saleType}</td>
                                <td>{product.productStatus}</td>
                                <td>{product.productName}</td>
                                <td>{product.description}</td>
                                <td>{product.price}</td>
                                <td>{product.conditionType}</td>
                                <td>{product.quantity}</td>
                                <td>{product.auctionEnd?.toLocaleString()}</td>
                                <td>{product.winningBid}</td>
                                <td>{product.offerPrice}</td>
                                <td>{product.userId}</td>
                                <td>{product.categoryIds}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>

            {error && <p className="text-red-600 mt-2">{error}</p>}
        </div>
    );
}

export default ProductByCategory;