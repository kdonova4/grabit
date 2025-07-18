import { useEffect, useState } from "react";
import { ReviewResponse } from "./types/Review/ReviewResponse";
import { Link, useParams } from "react-router-dom";
import { fetchReviewsBySeller } from "./api/ReviewAPI";
import { fetchProductById } from "./api/ProductApi";
import ReviewCard from "./ReviewCard";

interface ReviewListProps {
    sellerId: number;
}

export interface ProductSummary {
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
                    <ReviewCard key={review.reviewId} review={review} productMap={productMap}/>
                ))}
            </div>
        </>
    )
}

export default ReviewList;