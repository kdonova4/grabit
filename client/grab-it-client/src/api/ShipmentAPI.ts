import { ShipmentResponse } from "../types/Shipment/ShipmentResponse";

export async function fetchShipmentByOrder(orderId: number): Promise<ShipmentResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/shipments/order/${orderId}`);

    if(!response.ok) {
        throw new Error(`Error finding Order ${orderId}`);
    }

    const data: ShipmentResponse = await response.json();
    return data;
}

export async function fetchShipmentByTrackingNumber(trackingNumber: string): Promise<ShipmentResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/shipments/tracking/${trackingNumber}`);

    if(!response.ok) {
        throw new Error(`Cannot find tracking number: ${trackingNumber}`)
    }

    const data: ShipmentResponse = await response.json();
    return data;
}

export async function fetchShipmentById(shipmentId: number): Promise<ShipmentResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/shipments/${shipmentId}`);

    if(!response.ok) {
        throw new Error(`Shipment ID ${shipmentId} Not Found`)
    }

    const data: ShipmentResponse = await response.json();
    return data;
}

