import { useState } from "react";
import { AddressResponse } from "../../types/Address/AddressResponse";
import { fetchAddressById } from "../../api/AddressAPI";
import { useAuth } from "../../AuthContext";

const AddressById: React.FC = () => {

    const [id, setId] = useState("");
    const [address, setAddress] = useState<AddressResponse | null>(null);
    const [error, setError] = useState<string | null>(null);
    const { token } = useAuth();

    const handleFetch = async () => {
        try {
            if(!token) {
                throw new Error("User is not authenticated")
            }
            const data = await fetchAddressById(Number(id), token);
            setAddress(data);
        } catch (e) {
            setError((e as Error).message);
            setAddress(null);
        }
    }

    return (
        <>

            <div className="p-4">
                <h2 className="text-xl font-bold mb-2">Fetch Address By Address ID</h2>

                <input
                    type="number"
                    value={id}
                    onChange={(e) => setId(e.target.value)}
                    className="border px-2 py-1 mr-2"
                    placeholder="Enter Address ID"
                />
                <button onClick={handleFetch} className="bg-blue-600 text-white px-3 py-1 rounded">
                    Fetch
                </button>


                {address && (
                    <div className="mt-4 p-4 border rounded">
                        <p>{address.addressId}</p>
                        <p>{address.street}</p>
                        <p>{address.city}</p>
                        <p>{address.state}</p>
                        <p>{address.zipCode}</p>
                        <p>{address.country}</p>
                        <p>{address.userId}</p>
                    </div>

                )}
                {error && <p className="text-red-600 mt-2">{error}</p>}

            </div>
        </>
    )
}

export default AddressById;