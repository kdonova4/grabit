import { useState } from "react";
import { AddressCreateRequest } from "./types/Address/AddressCreateRequest";
import { useAuth } from "./AuthContext";
import { createAddress } from "./api/AddressAPI";
import AddressSelector from "./AddressSelector";

const ADDRESS_DEFAULT: AddressCreateRequest = {
    street: "",
    city: "",
    state: "",
    zipCode: "",
    country: "",
    userId: 0
}

const AddressForm: React.FC = () => {

    const [address, setAddress] = useState<AddressCreateRequest>(ADDRESS_DEFAULT);
    const [errors, setErrors] = useState<string[]>([]);
    const [showCreate, setShowCreate] = useState<boolean>(false);
    const [refreshAddresses, setRefreshAddresses] = useState<boolean>(false);
    const { token, appUserId } = useAuth();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        console.log("SUBMIT")
        addAddress();
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        setAddress(prev => ({
            ...prev,
            [name]: value
        }));
    }

    const addAddress = async () => {
        if (!token || !appUserId) {
            setErrors(["User Not Authenticated"]);
            return;
        }

        const createRequest: AddressCreateRequest = {
            street: address.street,
            city: address.city,
            state: address.state,
            zipCode: address.zipCode,
            country: address.country,
            userId: appUserId
        }

        try {
            const data = await createAddress(createRequest, token);
            setRefreshAddresses(prev => !prev)
            setShowCreate(false)
            console.log(data);
        } catch (e) {
            if (Array.isArray(e)) {
                setErrors(e)
            }
        }
    }

    const openCreate = () => {
        setShowCreate(true);
    }

    const closeCreate = () => {
        setShowCreate(false);
    }

    return (


        <>
            {!showCreate && (
                <button onClick={openCreate} className='btn btn-outline-success mr-4'>Add New Address</button>
            )}

            {showCreate && (
                <section>
                    <button onClick={closeCreate} className='btn btn-outline-success mr-4'>cancel</button>
                    <h2 className='mb-4'>"Add Address</h2>
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



                        <fieldset className="form-group">
                            <label htmlFor="street">Street</label>
                            <input
                                id="street"
                                name="street"
                                type="text"
                                className="formControl"
                                value={address.street}
                                onChange={handleChange}
                            />
                        </fieldset>
                        <fieldset className="form-group">
                            <label htmlFor="city">City</label>
                            <input
                                id="city"
                                name="city"
                                type="text"
                                className="formControl"
                                value={address.city}
                                onChange={handleChange}
                            />
                        </fieldset>
                        <fieldset className="form-group">
                            <label htmlFor="state">State</label>
                            <input
                                id="state"
                                name="state"
                                type="text"
                                className="formControl"
                                value={address.state}
                                onChange={handleChange}
                            />
                        </fieldset>
                        <fieldset className="form-group">
                            <label htmlFor="zipCode">Zip Code</label>
                            <input
                                id="zipCode"
                                name="zipCode"
                                type="text"
                                className="formControl"
                                value={address.zipCode}
                                onChange={handleChange}
                            />
                        </fieldset>
                        <fieldset className="form-group">
                            <label htmlFor="country">Country</label>
                            <input
                                id="country"
                                name="country"
                                type="text"
                                className="formControl"
                                value={address.country}
                                onChange={handleChange}
                            />
                        </fieldset>
                        <button type='submit' className='btn btn-outline-success mr-4'>Add Address</button>

                    </form>
                </section>
            )}
            <AddressSelector refreshTrigger={refreshAddresses} appUserId={appUserId}/>
        </>
    )
}

export default AddressForm;