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

const ProductPage: React.FC = () => {

    const [product, setProduct] = useState<ProductResponse | null>(null);
    const [categoryName, setCategoryName] = useState<string>("");
    const [images, setImages] = useState<ImageResponse[] | null>(null);
    const [addresses, setAddresses] = useState<AddressResponse[] | null>(null);
    const [value, setValue] = useState<number>(1);
    const [errors, setErrors] = useState<string[]>([]);
    const { id } = useParams();

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
    }, [id]);

    useEffect(() => {
        if (product) {
            fetchCategory();
            fetchAddress();
        }

    }, [product]);

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
                {product && (
                    <div className="container">
                        {images?.map(image => (
                            <img key={image.imageId} src={image.imageUrl}></img>
                        ))}
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
                        <p>{new Date(product.postedAt).toLocaleString()}</p>

                        {Array.isArray(addresses) && addresses.length > 0 && (
                            <section>
                                <p>Shipping From: {addresses[0].city}, {addresses[0].state} {addresses[0].zipCode} {addresses[0].country}</p>
                            </section>
                        )}

                        <Button type="submit">Add To Cart</Button>
                        <Button type="submit">Watch</Button>


                        <ReviewList sellerId={product.userId} />
                    </div>
                )}
            </div>

        </>
    )
}

export default ProductPage;