import { useEffect, useState } from "react";
import { useWatch } from "./WatchContext";
import { fetchProductById } from "./api/ProductApi";

export interface ProductSummary {
    productName: string;
    price: number;
}

const WatchPage: React.FC = () => {

    const { watchlist, addToWatchlist, removeFromWatchlist, itemInWatchlist } = useWatch();
    const [productMap, setProductMap] = useState<Record<number, ProductSummary>>({});


    useEffect(() => {
        if(watchlist) {
            fetchProducts();
        }
    }, [watchlist])
    
    const fetchProducts = async () => {
            const map: Record<number, ProductSummary> = {};
    
            for (const watchItem of watchlist) {
                try {
                    const product = await fetchProductById(watchItem.productId);
                    map[watchItem.watchId] = {
                        productName: product.productName,
                        price: product.price
                    }
                } catch (e) {
                    console.log(e)
                }
            }
    
            setProductMap(map);
        }


    return (
        <>
            <div>
                {watchlist.length > 0 ? (
                    <div>

                        {watchlist.map(watchItem => (

                            <div className="container" key={watchItem.watchId}>
                                {productMap[watchItem.watchId] && (
                                    <>
                                        <p>{productMap[watchItem.watchId].productName}</p>
                                        <p>{productMap[watchItem.watchId].price}</p>
                                        
                                        <p>------------------------</p>
                                    </>
                                )}
                            </div>

                        ))}
                    </div>
                ) : (
                    <></>
                )}
            </div>
        </>
    )
}

export default WatchPage;