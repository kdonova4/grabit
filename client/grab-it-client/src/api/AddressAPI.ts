import { AddressResponse } from "../types/Address/AddressResponse";
import { AddressCreateRequest } from "../types/Address/AddressCreateRequest";
import { AddressUpdateRequest } from "../types/Address/AddressUpdateRequest";

export async function fetchAddressByUser(userId: number): Promise<AddressResponse[]> {
    const response = await fetch(`http://localhost:8080/api/v1/addresses/user/${userId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        }
    });


    if (!response.ok) {
        throw new Error(`Error fetching addressess for user ${userId}`);
    }

    const data: AddressResponse[] = await response.json();
    return data;
}

export async function fetchAddressById(addressId: number, token: string): Promise<AddressResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/addresses/${addressId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });

    if (response.status === 403) {
        throw new Error("User Not Authenticated")
    }

    if (response.status === 404) {
        throw new Error(`Address ID ${addressId} Not Found`)
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

    if (response.status !== 201) {
        throw new Error("Error Creating new Address");
    }

    return await response.json();
}

export async function updateAddress(addressId: number, address: AddressUpdateRequest): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/addresses/${addressId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(address),
    });

    if (response.status === 409) {
        throw new Error("Address ID conflict")
    }

    if (!response.ok) {
        throw new Error("Error updating Address")
    }

}

