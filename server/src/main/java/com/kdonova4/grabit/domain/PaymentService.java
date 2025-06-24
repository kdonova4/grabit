package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.data.PaymentRepository;
import com.kdonova4.grabit.domain.mapper.PaymentMapper;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.model.Payment;
import com.kdonova4.grabit.model.PaymentCreateDTO;
import com.kdonova4.grabit.model.PaymentResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final OrderRepository orderRepository;


    public PaymentService(PaymentRepository repository, OrderRepository orderRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
    }

    public List<Payment> findAll() {
        return repository.findAll();
    }

    public Optional<Payment> findByOrder(Order order) {
        return repository.findByOrder(order);
    }

    public List<Payment> findByAmountPaidGreaterThan(BigDecimal amount) {
        return repository.findByAmountPaidGreaterThan(amount);
    }

    public List<Payment> findByAmountPaidLessThan(BigDecimal amount) {
        return repository.findByAmountPaidLessThan(amount);
    }

    public Optional<Payment> findById(int id) {
        return repository.findById(id);
    }

    public Result<PaymentResponseDTO> create(Payment payment) {
        Result<PaymentResponseDTO> result = validate(payment);

        if(!result.isSuccess())
            return result;

        if(payment.getPaymentId() != 0) {
            result.addMessages("PaymentId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        payment = repository.save(payment);
        result.setPayload(PaymentMapper.toResponse(payment));
        return result;
    }

    private Result<PaymentResponseDTO> validate(Payment payment) {
        Result<PaymentResponseDTO> result = new Result<>();

        if(payment == null) {
            result.addMessages("PAYMENT CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(payment.getOrder() == null || payment.getOrder().getOrderId() <= 0) {
            result.addMessages("ORDER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        if(payment.getAmountPaid() == null) {
            result.addMessages("AMOUNT PAID IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Order> order = orderRepository.findById(payment.getOrder().getOrderId());

        if(order.isEmpty()) {
            result.addMessages("ORDER MUST EXIST", ResultType.INVALID);
        }else if(payment.getAmountPaid().compareTo(order.get().getTotalAmount()) != 0) {
            result.addMessages("AMOUNT PAID MUST EQUAL TO ORDER TOTAL", ResultType.INVALID);
        }


        return result;
    }
}
