import { AddressResponse } from "../types/Address/AddressResponse";
import { AddressCreateRequest } from "../types/Address/AddressCreateRequest";
import { AddressUpdateRequest } from "../types/Address/AddressUpdateRequest";

export async function fetchAddressByUser(userId: number): Promise<AddressResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/addresses/user/${userId}`);

    if(!response.ok) {
        throw new Error(`Error fetching addressess for user ${userId}`);
    }

    const data: AddressResponse[] = await response.json();
    return data;
}

export async function fetchAddressById(addressId: number): Promise<AddressResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/addresses/${addressId}`);

    if(!response.ok) {
        throw new Error(`Error finding address with ID ${addressId}`);
    }

    const data: AddressResponse = await response.json();
    return data;
}

export async function addAddress(address: AddressCreateRequest): Promise<AddressResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/addresses`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(address)
    });

    if(response.status !== 201) {
        throw new Error("Error Creating new Address");
    }

    return await response.json();
}

export async function updateAddress(addressId: number , address: AddressUpdateRequest): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/addresses/${addressId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(address),
    });

    if(response.status === 409) {
        throw new Error("Address ID conflict")
    }

    if(!response.ok) {
        throw new Error("Error updating Address")
    }

}

