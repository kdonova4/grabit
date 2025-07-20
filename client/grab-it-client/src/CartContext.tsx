import { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { ShoppingCartDTO } from "./types/ShoppingCart/ShoppingCartDTO";
import { jwtDecode, JwtPayload } from "jwt-decode";
import { useAuth } from "./AuthContext";
import { createCartItem, deleteCartItemById, findCartItemByUserAndProduct, findCartItemsByUser } from "./api/ShoppingCartAPI";

interface CartContextType {
    cart: ShoppingCartDTO[];
    addToCart: (productId: number, quantity: number) => void;
    itemInCart: (appUserId: number, productId: number) => Promise<boolean>;
    updateCartItem: (cartId: number, cart: ShoppingCartDTO) => void;
    removeFromCart: (cartId: number) => void;
    fetchCart: () => void;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

interface CartProviderProps {
    children: ReactNode;
}

export const CartProvider: React.FC<CartProviderProps> = ({ children }) => {

    const [cart, setCart] = useState<ShoppingCartDTO[]>([]);
    const { token, appUserId } = useAuth();
    const [errors, setErrors] = useState<string[]>([])
    const decodedToken = token ? jwtDecode<JwtPayload>(token) : null;

    useEffect(() => {
        if (token) {
            fetchCart();
        }
    }, [token, appUserId])


    const fetchCart = async () => {

        if (token && appUserId) {
            try {
                const data = await findCartItemsByUser(appUserId, token);
                setCart(data)
                console.log(data)
            } catch (e) {
                console.log(e)
                setErrors((prev => [...prev, (e as Error).message]));
            }
        }
    }

    const addToCart = async (productId: number, quantity: number) => {

        if (token && appUserId) {

            const cartItem: ShoppingCartDTO = {
                shoppingCartId: 0,
                productId: productId,
                userId: appUserId,
                quantity: quantity
            }

            try {
                const data = await createCartItem(cartItem, token)
                setCart((prev => [...prev, data]))
            } catch (e) {
                if (Array.isArray(e)) {
                    setErrors(e);
                }
            }
        }
    }

    const itemInCart = async (appUserId: number, productId: number): Promise<boolean> => {
        if (token) {
            try {
                const data = cart.find((item) => item.productId === productId);
                if (data) {
                    return true;
                } else {
                    return false;
                }
            } catch (e) {
                if (Array.isArray(e)) {
                    setErrors(e);
                }

                return false;
            }

        }

        return false;
    }

    const updateCartItem = async (cartId: number, cart: ShoppingCartDTO) => {
        if (token && appUserId) {
            try {
                await updateCartItem(cartId, cart);
            } catch (e) {
                if (Array.isArray(e)) {
                    setErrors(e);
                }
            }
        }
    }

    const removeFromCart = async (productId: number) => {
        if (token && appUserId) {

            const itemToDelete = cart.find((item) => item.productId === productId);

            if (!itemToDelete) {
                setErrors(["Cart Item Not Found"]);
                return;
            }

            try {
                await deleteCartItemById(itemToDelete.shoppingCartId, token);
                const newCart = cart.filter(cartItem => cartItem.shoppingCartId !== itemToDelete.shoppingCartId)
                setCart(newCart);
            } catch (e) {
                if (Array.isArray(e)) {
                    setErrors(e);
                }
            }



        }
    }

    return (
        <>
            <CartContext.Provider value={{ cart, addToCart, updateCartItem, itemInCart, removeFromCart, fetchCart }}>
                {children}
            </CartContext.Provider>
        </>
    )
}

export const useCart = (): CartContextType => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error("useCart must be used within a CartProvider");
    }

    return context;
}