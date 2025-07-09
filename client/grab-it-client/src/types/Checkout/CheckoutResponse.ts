import { OrderResponse } from "../Order/OrderResponse";
import { ShipmentResponse } from "../Shipment/ShipmentResponse";

export interface CheckoutResponse {
    orderResponseDTO: OrderResponse;
    shipmentResponseDTO: ShipmentResponse;
    paymentResponseDTO: PaymentResponse;
}