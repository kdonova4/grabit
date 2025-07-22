import { useEffect, useState } from "react";
import { ProductResponse } from "./types/Product/ProductResponse";
import { data, useParams } from "react-router-dom";
import { fetchProductById } from "./api/ProductApi";
import ReviewList from "./ReviewList";
import { fetchCategoryById } from "./api/CategoryAPI";
import { Button } from "react-bootstrap";
import { ImageResponse } from "./types/Image/ImageResponse";
import { fetchImagesByProduct } from "./api/ImageAPI";
import { AddressResponse } from "./types/Address/AddressResponse";
import { fetchAddressByUser } from "./api/AddressAPI";
import { useAuth } from "./AuthContext";
import { useCart } from "./CartContext";
import { useWatch } from "./WatchContext";
import BidList from "./BidList";
import BidForm from "./BidForm";

const ProductPage: React.FC = () => {

    const [product, setProduct] = useState<ProductResponse | null>(null);
    const [categoryName, setCategoryName] = useState<string>("");
    const [images, setImages] = useState<ImageResponse[] | null>(null);
    const [addresses, setAddresses] = useState<AddressResponse[] | null>(null);
    const [value, setValue] = useState<number>(1);
    const [errors, setErrors] = useState<string[]>([]);
    const [inCart, setInCart] = useState<boolean | undefined>(undefined);
    const [inWatch, setInWatch] = useState<boolean | undefined>(undefined);
    const { id } = useParams();
    const { token, appUserId } = useAuth();
    const { itemInCart, addToCart, removeFromCart, cart } = useCart();
    const { itemInWatchlist, addToWatchlist, removeFromWatchlist, watchlist } = useWatch();

    const [showModal, setShowModal] = useState<boolean>(false);

    const handleOpenModal = () => {
        setShowModal(true);
    }

    const handleCloseModal = () => {
        setShowModal(false);
    }

    const fetchProduct = async () => {
        try {
            const data = await fetchProductById(Number(id));
            setProduct(data);
        } catch (e) {
            if (Array.isArray(e)) {
                setErrors(e);
            }
        }
    }

    const fetchImages = async () => {
        try {
            const data = await fetchImagesByProduct(Number(id));
            setImages(data);
        } catch (e) {
            if (Array.isArray(e)) {
                setErrors(e);
            }
        }
    }

    const fetchAddress = async () => {
        try {
            if (product) {
                const data = await fetchAddressByUser(product.userId);
                setAddresses(data);
            }


        } catch (e) {
            if (Array.isArray(e)) {
                setErrors(e);
            }
        }
    }

    useEffect(() => {
        fetchProduct();
        fetchImages();
        if(product) {

            console.log(inCart)
        }
        
        
    }, [id, cart, watchlist]);

    useEffect(() => {
        if (product) {
            fetchCategory();
            fetchAddress();
            
            
        }

    }, [product, cart, watchlist]);

    useEffect(() => {
        checkCart();
    }, [cart, id]);

    useEffect(() => {
        checkWatch();
    }, [watchlist, id]);

    const checkCart = async () => {
        console.log(inCart)
        if (token && appUserId) {
            try {
                const result = await itemInCart(appUserId, Number(id));
                setInCart(result);
            } catch (e) {
                console.log(e);
            }
        }
    }

    const checkWatch = async () => {
        
        if (token && appUserId) {
            try {
                const result = await itemInWatchlist(Number(id));
                setInWatch(result);
            } catch (e) {
                console.log(e);
            }
        }
    }

    const fetchCategory = async () => {
        if (product) {
            try {
                const data = await fetchCategoryById(product.categoryId)
                setCategoryName(data.categoryName);
            } catch (e) {
                if (Array.isArray(e)) {
                    setErrors(e);
                }
            }

        }
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>
    ) => {
        const { name, value } = e.target;
        setValue(Number(e.target.value))

    }



    return (


        <>
            <div>
                {product && product.productStatus === "ACTIVE" ? (

                    <div className="container">
                        {images?.map(image => (
                            <img key={image.imageId} src={image.imageUrl}></img>
                        ))}
                        {product.saleType === "BUY_NOW" ? (
                            <section>
                                <p>{product.productName}</p>
                                <p>${product.price.toFixed(2)}</p>
                                <p>{product.description}</p>
                                <p>Category: {categoryName}</p>
                                <p>Condition: {product.conditionType}</p>
                                <fieldset className="form-group">
                                    <label htmlFor="quantity">Quantity: &nbsp;</label>
                                    <input
                                        type="number"
                                        className="formControl"
                                        value={value}
                                        max={product.quantity}
                                        min={1}
                                        onChange={handleChange}
                                    /><p>{product.quantity} available</p>
                                </fieldset>
                                <p>Posted On: {new Date(product.postedAt).toLocaleString()}</p>

                                {Array.isArray(addresses) && addresses.length > 0 && (
                                    <section>
                                        <p>Shipping From: {addresses[0].city}, {addresses[0].state} {addresses[0].zipCode} {addresses[0].country}</p>
                                    </section>
                                )}

                                {inCart !== undefined && (

                                
                                <div className="cart-section">
                                    
                                        {inCart ? (
                                            <Button
                                                className="remove-wishlist"
                                                variant="danger"
                                                onClick={() => removeFromCart(Number(id))}
                                            >
                                                Remove From Cart
                                            </Button>
                                        ) : (
                                            <Button
                                                className="add-wishlist"
                                                variant="success"
                                                onClick={() => addToCart(Number(id), value)}
                                            >
                                                Add To Cart
                                            </Button>
                                        )}
                                    
                                </div>)}

                                {inWatch !== undefined && (

                                
                                <div className="watch-selection">
                                    
                                        {(!inWatch ? (
                                            <Button
                                                className="add-watch"
                                                variant="light"
                                                onClick={() => addToWatchlist(Number(id))}
                                            >Watch</Button>
                                        ) : (
                                            <Button
                                                className="remove-watch"
                                                variant="light"
                                                onClick={() => removeFromWatchlist(Number(id))}
                                            >Un-Watch</Button>
                                        ))}
                                </div>)}
                                <Button type="submit">Make Offer</Button>


                            </section>
                        ) : (
                            <section>
                                <p>{product.productName}</p>
                                <p>${product.price.toFixed(2)}</p>
                                <p>{product.description}</p>
                                {product.auctionEnd && (
                                    <p>Auction End Date: {new Date(product.auctionEnd).toLocaleString()}</p>
                                )}
                                <p>Category: {categoryName}</p>
                                <p>Condition: {product.conditionType}</p>
                                <fieldset className="form-group">
                                    <label htmlFor="quantity">Quantity: &nbsp;</label>
                                    <input
                                        type="number"
                                        className="formControl"
                                        value={value}
                                        max={product.quantity}
                                        min={1}
                                        onChange={handleChange}
                                    /><p>{product.quantity} available</p>
                                </fieldset>
                                <p>Posted On: {new Date(product.postedAt).toLocaleString()}</p>

                                {Array.isArray(addresses) && addresses.length > 0 && (
                                    <section>
                                        <p>Shipping From: {addresses[0].city}, {addresses[0].state} {addresses[0].zipCode} {addresses[0].country}</p>
                                    </section>
                                )}

                                <Button type="submit" onClick={handleOpenModal}>Place Bid</Button>
                                {inWatch !== undefined && (

                                
                                <div className="watch-selection">
                                    
                                        {(!inWatch ? (
                                            <Button
                                                className="add-watch"
                                                variant="light"
                                                onClick={() => addToWatchlist(Number(id))}
                                            >Watch</Button>
                                        ) : (
                                            <Button
                                                className="remove-watch"
                                                variant="light"
                                                onClick={() => removeFromWatchlist(Number(id))}
                                            >Un-Watch</Button>
                                        ))}
                                </div>)}
                            </section>
                        )}



                        <BidList/>
                        <ReviewList sellerId={product.userId} />

                        {showModal && (
                            <BidForm onClose={handleCloseModal} showModal={showModal}/>
                        )}
                    </div>
                ) : (
                    <p>Product Not Available</p>
                )}
            </div>

        </>
    )
}

export default ProductPage;