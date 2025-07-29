import { useState } from "react";
import { ProductResponse } from "./types/Product/ProductResponse";
import { ShoppingCartDTO } from "./types/ShoppingCart/ShoppingCartDTO";

const CheckoutPage: React.FC = () => {

    
    const [cart, setCart] = useState<ShoppingCartDTO[]>([]);


    return (
        <>

        </>
    )
}

export default CheckoutPage;