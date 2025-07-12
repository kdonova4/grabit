import { useState } from "react";
import { BidResponse } from "../../types/Bid/BidResponse";
import { fetchByUser } from "../../api/BidAPI";
import { useAuth } from "../../AuthContext";

const BidByUser: React.FC = () => {

    const [userId, setUserId] = useState("");
    const [bids, setBids] = useState<BidResponse[] | null>(null);
    const [errors, setErrors] = useState<string | null>(null);
    const { token } = useAuth();
    const handleFetch = async () => {
        try{
            if (!token) {
                throw new Error("User is not authenticated");
            }
            const data = await fetchByUser(Number(userId), token);
            setErrors(null);
            setBids(data);
        } catch(e) {
            setErrors((e as Error).message)
            setBids(null);
        }
    }

    return(
        <>

            <div className="p-4">
                <h2 className="text-xl font-bold mb-2">Fetch Bids By User ID</h2>
                <input
                    type="number"
                    value={userId}
                    onChange={(e) => setUserId(e.target.value)}
                    className="border px-2 py-1 mr-2"
                    placeholder="Enter User ID"
                />
                <button onClick={handleFetch} className="bg-blue-600 text-white px-3 py-1 rounded">
                    Fetch
                </button>


                {bids?.map(bid => (
                    <div key={bid.bidId} className="mt-4 p-4 border rounded">
                        <p>{bid.bidAmount}</p>
                        <p>{bid.placedAt?.toLocaleString()}</p>
                        <p>{bid.productId}</p>
                        <p>{bid.userId}</p>

                    </div>
                ))}


                {errors && <p className="text-red-600 mt-2">{errors}</p>}
            </div>
        </>
    )
}

export default BidByUser;