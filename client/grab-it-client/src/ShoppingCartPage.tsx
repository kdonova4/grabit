import { useEffect, useState } from "react";
import { useAuth } from "./AuthContext";
import { useCart } from "./CartContext";
import { ProductResponse } from "./types/Product/ProductResponse";
import { fetchProductById } from "./api/ProductApi";
import { Button } from "react-bootstrap";
import { Link } from "react-router-dom";


export interface ProductSummary {
    productName: string;
    price: number;
}

const ShoppingCartPage: React.FC = () => {

    const { removeFromCart, itemInCart, updateCartItem, fetchCart, cart } = useCart();
    const { token, appUserId } = useAuth();
    const [productMap, setProductMap] = useState<Record<number, ProductSummary>>({});
    useEffect(() => {
        if (token && appUserId) {
            fetchCart();
        }


    }, [appUserId])

    useEffect(() => {
        if(cart) {
            fetchProducts();
        }
    }, [cart])


    const fetchProducts = async () => {
        const map: Record<number, ProductSummary> = {};

        for (const cartItem of cart) {
            try {
                const product = await fetchProductById(cartItem.productId);
                map[cartItem.shoppingCartId] = {
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
            <div className="container">
                {cart.length > 0 ? (
                    <div>

                        {cart.map(cartItem => (

                            <div className="container" key={cartItem.shoppingCartId}>
                                {productMap[cartItem.shoppingCartId] && (
                                    <>
                                        <p>{productMap[cartItem.shoppingCartId].productName}</p>
                                        <p>{productMap[cartItem.shoppingCartId].price}</p>
                                        <p>{cartItem.quantity}</p>
                                        <p>------------------------</p>
                                    </>
                                )}
                            </div>

                        ))}
                        <Link className='btn btn-primary mr-4' type="button" to={'/checkout'}>Checkout</Link>
                    </div>
                ) : (
                    <></>
                )}
                
            </div>

            
        </>
    )
}

export default ShoppingCartPage;