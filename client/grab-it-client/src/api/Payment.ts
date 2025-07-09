export async function fetchPaymentByOrder(orderId: number): Promise<PaymentResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/payments/order/${orderId}`)

    if(!response.ok) {
        throw new Error(`Cannot Find Order ${orderId}`)
    }

    const data: PaymentResponse = await response.json();
    return data;
}

export async function fetchPaymentById(paymentId: number): Promise<PaymentResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/payments/${paymentId}`);

    if(!response.ok) {
        throw new Error(`Payment ID ${paymentId} Not Found`)
    }

    const data: PaymentResponse = await response.json();
    return data;
}
