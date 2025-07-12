import { useState } from "react";
import { AddressResponse } from "../../types/Address/AddressResponse";
import { fetchAddressByUser } from "../../api/AddressAPI";

const AddressByUser: React.FC = () => {

    const [userId, setUserId] = useState("");
    const [addresses, setAddresses] = useState<AddressResponse[] | null>(null);
    const [error, setError] = useState<string | null>(null);

    const handleFetch = async () => {
        try {
            const data = await fetchAddressByUser(Number(userId));
            setAddresses(data);

        } catch(e) {
            setAddresses(null);
            setError((e as Error).message);
        }
    }

    return (
        <>

            <div className="p-4">
                <h2 className="text-xl font-bold mb-2">Fetch Addresses By User ID</h2>
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


                {addresses?.map(address => (
                    <div key={address.addressId} className="mt-4 p-4 border rounded">
                        <p>{address.addressId}</p>
                        <p>{address.street}</p>
                        <p>{address.city}</p>
                        <p>{address.state}</p>
                        <p>{address.zipCode}</p>
                        <p>{address.country}</p>
                        <p>{address.userId}</p>
                    </div>
                ))}


                {error && <p className="text-red-600 mt-2">{error}</p>}
            </div>
        </>
    )
}

export default AddressByUser;