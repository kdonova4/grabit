export type ShipmentStatus = 'PENDING' | 'SHIPPED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED';

export interface ShipmentResponse {
    shipmentId: number;
    orderId: number;
    shipmentStatus: ShipmentStatus;
    shippedAt: string;
    deliveredAt: string;
}