import { Link } from "react-router-dom";
import { ProductSummary } from "./ReviewList";
import { ReviewResponse } from "./types/Review/ReviewResponse";

interface ReviewCardProps {
    review: ReviewResponse;
    productMap: Record<number, ProductSummary>;
}

const ReviewCard: React.FC<ReviewCardProps> = ({ review, productMap }) => {

    return (
        <>
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
        </>
    )
}

export default ReviewCard;