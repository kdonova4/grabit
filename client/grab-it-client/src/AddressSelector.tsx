import { useEffect, useState } from "react";
import { AddressResponse } from "./types/Address/AddressResponse";
import { fetchAddressByUser } from "./api/AddressAPI";
import { Card } from "react-bootstrap";

interface AddressSelectorProps {
    refreshTrigger: boolean,
    appUserId: number | null
}

const AddressSelector: React.FC<AddressSelectorProps> = ({ refreshTrigger, appUserId }) => {

    const [addresses, setAddresses] = useState<AddressResponse[]>([]);
    const [selectAddress, setSelectedAddress] = useState<number | null>(null);
    const [errors, setErrors] = useState<string[]>([]);

    useEffect(() => {
        fetchAddresses();
        console.log("Addresses", addresses)
    }, [refreshTrigger, appUserId])

    const fetchAddresses = async () => {
        if(appUserId) {
            try {
                const data = await fetchAddressByUser(appUserId)
                setAddresses(data)
            } catch(e) {
                console.log(e)
            }
        }
    }

    const chooseAddress = (addressId: number) => {

        if(selectAddress === addressId) {
            setSelectedAddress(null);
            console.log("De-Select")
        } else {
            setSelectedAddress(addressId);
        console.log("Selected Address ID: ", addressId)
        }
        
    }



    return(
        <>
        
            {addresses.map(address => (
                <div onClick={() => chooseAddress(address.addressId)} key={address.addressId}>
                    <p>{address.street}</p>
                    <p>{address.city}</p>
                    <p>{address.state}</p>
                    <p>{address.zipCode}</p>
                    <p>{address.country}</p>
                    <p>---------------------</p>
                </div>
            ))}
            
        
            
        </>
    )
}

export default AddressSelector;