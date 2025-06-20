package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.Payment;
import com.kdonova4.grabit.model.PaymentResponseDTO;

public class PaymentMapper {

    public static PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getPaymentId(),
                payment.getOrder().getOrderId(),
                payment.getAmountPaid(),
                payment.getPaidAt());
    }
}
