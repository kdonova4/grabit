package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.entity.Order;
import com.kdonova4.grabit.model.entity.Payment;
import com.kdonova4.grabit.model.dto.PaymentCreateDTO;
import com.kdonova4.grabit.model.dto.PaymentResponseDTO;

public class PaymentMapper {

    public static PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getPaymentId(),
                payment.getOrder().getOrderId(),
                payment.getAmountPaid(),
                payment.getPaidAt());
    }

    public static Payment toPayment(PaymentCreateDTO paymentCreateDTO, Order order) {
        return new Payment(
                0,
                order,
                paymentCreateDTO.getPaidAmount(),
                null
        );
    }

    public static Payment toPayment(PaymentResponseDTO paymentResponseDTO, Order order) {
        return new Payment(
                paymentResponseDTO.getPaymentId(),
                order,
                paymentResponseDTO.getAmountPaid(),
                paymentResponseDTO.getPaidAt()
        );
    }
}
