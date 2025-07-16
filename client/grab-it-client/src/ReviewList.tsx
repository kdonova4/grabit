import { useEffect, useState } from "react";
import { ReviewResponse } from "./types/Review/ReviewResponse";
import { Link, useParams } from "react-router-dom";
import { fetchReviewsBySeller } from "./api/ReviewAPI";
import { fetchProductById } from "./api/ProductApi";

interface ReviewListProps {
    sellerId: number;
}

interface ProductSummary {
    productName: string;
    productStatus: string;
}

const ReviewList: React.FC<ReviewListProps> = ({ sellerId }) => {
    const [reviews, setReviews] = useState<ReviewResponse[]>([])
    const [errors, setErrors] = useState<string[]>([]);
    const [productMap, setProductMap] = useState<Record<number, ProductSummary>>({});
    const { id } = useParams();

    const fetchReviews = async () => {
        try {
            const data = await fetchReviewsBySeller(sellerId);
            setReviews(data);

            const uniqueProductIds = [...new Set(data.map(r => r.productId))];

            const productSummaryMap: Record<number, ProductSummary> = {};

            await Promise.all(uniqueProductIds.map(async id => {
                try {
                    const product = await fetchProductById(id);
                    productSummaryMap[id] = {
                        productName: product.productName,
                        productStatus: product.productStatus
                    };
                } catch (e) {
                    setErrors([(e as Error).message]);
                }
            }));

            setProductMap(productSummaryMap);
        } catch (e) {
            if (Array.isArray(e)) {
                setErrors(e)
            }
        }
    }


    useEffect(() => {
        fetchReviews();
    }, [sellerId])

    return (
        <>

            <div className="container">
                <h2>Reviews For This Seller</h2>

                {errors.length > 0 && (
                    <div className="alert alert-danger">
                        {errors.map((e, i) => <div key={i}>{e}</div>)}
                    </div>
                )}

                {reviews.map(review => (
                    <div key={review.reviewId} className="mt-4 p-4 border rounded">
                        <p>Rating: {review.rating}</p>


                        {productMap[review.productId]?.productStatus === "ACTIVE" ? (

                            <p>
                                Product:{" "}
                                <Link to={`/product/${review.productId}`}>
                                    {productMap[review.productId].productName}
                                </Link>
                            </p>
                        ) : (
                            <span>Product: {productMap[review.productId]?.productName}</span>
                        )}
                        <p className="mt-4">{review.reviewText}</p>
                        <p>{new Date(review.createdAt).toLocaleString()}</p>
                    </div>
                ))}
            </div>
        </>
    )
}

export default ReviewList;