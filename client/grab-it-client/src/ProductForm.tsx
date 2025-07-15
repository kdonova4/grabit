import { useEffect, useState } from "react";
import { ProductCreateRequest } from "./types/Product/ProductCreateRequest";
import { data, Link, useNavigate, useParams } from "react-router-dom";
import { createProduct, fetchProductById, updateProduct } from "./api/ProductApi";
import { ProductResponse } from "./types/Product/ProductResponse";
import { ProductUpdateRequest } from "./types/Product/ProductUpdateRequest";
import { useAuth } from "./AuthContext";
import { ProductFormState } from "./types/Product/ProductFormState";
import ImageUpload from "./ImageUpload";
import { deleteImageById, fetchImagesByProduct, uploadImage } from "./api/ImageAPI";
import { ImageResponse } from "./types/Image/ImageResponse";


const PRODUCT_DEFAULT: ProductFormState = {
    productId: 0,
    saleType: "BUY_NOW",
    productName: "",
    description: "",
    price: 0.00,
    condition: "NEW",
    quantity: 1,
    status: "ACTIVE",
    winningBid: null,
    userId: 0,
    categoryIds: []
}



const ProductForm: React.FC = () => {


    const [pendingDeletes, setPendingDeletes] = useState<number[]>([]);
    const [images, setImages] = useState<ImageResponse[]>([]);
    const [files, setFiles] = useState<File[]>([]);
    const [product, setProduct] = useState<ProductFormState>(PRODUCT_DEFAULT);
    const [errors, setErrors] = useState<string[]>([]);
    const navigate = useNavigate();
    const { id } = useParams();
    const [newId, setNewId] = useState<number>(0);
    const { token, appUserId } = useAuth();


    const fetchImages = async () => {
        try {
            const data = await fetchImagesByProduct(Number(id));
            console.log(data)

            setImages(data)
        } catch (e) {
            setErrors([(e as Error).message])
        }
    }

    useEffect(() => {

        if (id) {
            fetchImages();
            const data = fetchProductById(Number(id))
                .then((data: ProductResponse) => {
                    const editProduct: ProductFormState = {
                        productId: data.productId,
                        productName: data.productName,
                        description: data.description,
                        price: data.price,
                        condition: data.conditionType,
                        status: data.productStatus,
                        winningBid: data.winningBid,
                        quantity: data.quantity,
                        userId: data.userId,
                        saleType: data.saleType,
                        categoryIds: data.categoryIds
                    };
                    setProduct(editProduct);
                })
                .catch((e) => {
                    console.error("Error fetching product: ", e)
                    setErrors(e);
                })

        } else {
            setProduct(PRODUCT_DEFAULT);
            setImages([])
        }
    }, [id])

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        

        if (!token) {
            setErrors(["User Not Authenticated"]);
            return;
        }

        let productId: number | undefined;

        const totalImages = images.length + files.length;
        if (totalImages === 0) {
            setErrors(["Need at least one image"]);
            return;
        }
      
        if (id) {
            const success = await editProduct();
            productId = Number(id);
            if (!success) return;
        } else {

            productId = await addProduct();
        }

        if (!productId) return;

        

        if (files.length > 0) {
            try {
                await uploadImage(productId, files, token);
                setFiles([]); 
            } catch (e) {
                setErrors(["Image upload failed"]);
                return;
            }
        }

        for (const imageId of pendingDeletes) {
            try {
                await deleteImageById(imageId, token);
            } catch (e) {
                console.error(`Failed to delete image ${imageId}`, e);
            }
        }
        setPendingDeletes([]);

        navigate('/');
    };



    const addProduct = async (): Promise<number | undefined> => {
        if (!token) {
            setErrors(["User Not Authenticated"]);
            return;
        }

        const createRequest: ProductCreateRequest = {
            productName: product.productName,
            price: product.price,
            conditionType: product.condition,
            userId: appUserId!,
            saleType: product.saleType,
            description: product.description,
            quantity: product.quantity,
            categoryIds: product.categoryIds,
        }

        try {
            const data = await createProduct(createRequest, token);
            console.log(data);
            return data.productId;
        } catch (e) {
            if(Array.isArray(e)) {
                setErrors(e)
            }
            return undefined;
        }

    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement> | React.ChangeEvent<HTMLSelectElement>
    ) => {
        const { name, value } = e.target;

        setProduct(prev => ({
            ...prev,
            [name]: name === "price" || name === "quantity" ? Number(value) : value
        }));
    }

    const editProduct = async (): Promise<boolean> => {

        if (!token) {
            setErrors(["Problem Updating Product"]);
            return false;
        }

        const updateRequest: ProductUpdateRequest = {
            productId: product.productId,
            productName: product.productName,
            description: product.description,
            price: product.price,
            condition: product.condition,
            status: product.status,
            winningBid: product.winningBid,
            quantity: product.quantity,
            categoryIds: product.categoryIds,
        }

        try {
            const data = await updateProduct(Number(id), updateRequest, token);
            if (data) {
                setErrors((data))
                return false;
            }
            return true;
        } catch (e) {
            setErrors([(e as Error).message])
            return false;
        }


    }



    return (
        <>
            <section>
                <h2 className='mb-4'>{id ? 'Update Product' : 'Add Product'}</h2>
                {errors.length > 0 && (
                    <div className='alert alert-danger'>
                        <p>The following errors were found: </p>
                        <ul>
                            {errors.map(error => (
                                <li key={error}>{error}</li>
                            ))}
                        </ul>
                    </div>
                )}

                <form onSubmit={handleSubmit}>

                    {!id && (
                        <fieldset className="form-group">
                            <label htmlFor="saleType">Sale Type</label>
                            <select
                                id="saleType"
                                className="form-control"
                                name="saleType"
                                value={product.saleType}
                                onChange={handleChange}
                            >
                                <option>BUY_NOW</option>
                                <option>AUCTION</option>
                            </select>
                        </fieldset>
                    )}
                    <fieldset className="form-group">
                        <label htmlFor="productName">Product Name</label>
                        <input
                            id="productName"
                            name="productName"
                            type="text"
                            className="formControl"
                            value={product.productName}
                            onChange={handleChange}
                        />
                    </fieldset>
                    <fieldset className="form-group">
                        <label htmlFor="description">Description</label>
                        <input
                            id="description"
                            name="description"
                            type="text"
                            className="formControl"
                            value={product.description}
                            onChange={handleChange}
                        />
                    </fieldset>
                    <fieldset className="form-group">
                        <label htmlFor="price">Price</label>
                        <input
                            id="price"
                            name="price"
                            type="number"
                            className="formControl"
                            value={product.price}
                            onChange={handleChange}
                        />
                    </fieldset>
                    <fieldset className="form-group">
                        <label htmlFor="condition">Condition</label>
                        <select
                            id="condition"
                            className="form-control"
                            name="condition"
                            value={product.condition}
                            onChange={handleChange}
                        >
                            <option>NEW</option>
                            <option>GOOD</option>
                            <option>EXCELLENT</option>
                            <option>FAIR</option>
                            <option>USED</option>
                            <option>REFURBISHED</option>
                            <option>DAMAGED</option>
                        </select>
                    </fieldset>
                    <fieldset className="form-group">
                        <label htmlFor="quantity">Quantity</label>
                        <input
                            id="quantity"
                            name="quantity"
                            type="number"
                            className="formControl"
                            value={product.quantity}
                            onChange={handleChange}
                        />
                    </fieldset>
                    {id && (
                        <fieldset className="form-group">
                            <label htmlFor="status">Product Status</label>
                            <select
                                id="status"
                                className="form-control"
                                name="status"
                                value={product.status}
                                onChange={handleChange}
                            >
                                <option>ACTIVE</option>
                                <option>REMOVED</option>
                                <option>HELD</option>
                            </select>
                        </fieldset>
                    )}
                    <fieldset className='form-group'>
                        <ImageUpload
                            pendingDeletes={pendingDeletes}
                            setPendingDeletes={setPendingDeletes}
                            fetchImages={fetchImages} images={images} setImages={setImages} files={files} setFiles={setFiles} />
                        <button type='submit' className='btn btn-outline-success mr-4'>{id ? 'Update Game' : 'Add Game'}</button>
                        <Link type='button' className='btn btn-outline-danger mr-4' to={'/'}>Cancel</Link>
                    </fieldset>
                </form>
            </section>
        </>
    )
}

export default ProductForm;