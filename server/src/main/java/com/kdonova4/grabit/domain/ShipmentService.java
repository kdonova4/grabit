package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.OrderRepository;
import com.kdonova4.grabit.data.ShipmentRepository;
import com.kdonova4.grabit.domain.mapper.ShipmentMapper;
import com.kdonova4.grabit.enums.ShipmentStatus;
import com.kdonova4.grabit.model.Order;
import com.kdonova4.grabit.model.Shipment;
import com.kdonova4.grabit.model.ShipmentCreateDTO;
import com.kdonova4.grabit.model.ShipmentResponseDTO;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository repository;
    private final OrderRepository orderRepository;

    public ShipmentService(ShipmentRepository repository, OrderRepository orderRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
    }

    public List<Shipment> findAll() {
        return repository.findAll();
    }

    public Optional<Shipment> findByOrder(Order order) {
        return repository.findByOrder(order);
    }

    public List<Shipment> findByShipmentStatus(ShipmentStatus status) {
        return repository.findByShipmentStatus(status);
    }

    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber);
    }

    public Optional<Shipment> findById(int id) {
        return repository.findById(id);
    }

    public Result<ShipmentResponseDTO> create(Shipment shipment) {
        Result<ShipmentResponseDTO> result = validate(shipment);


        if(!result.isSuccess())
            return result;

        if(shipment.getShipmentId() != 0) {
            result.addMessages("ShipmentId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        String trackingNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 18);

        shipment.setTrackingNumber(trackingNumber);

        shipment = repository.save(shipment);
        result.setPayload(ShipmentMapper.toResponse(shipment));
        return result;
    }

    public Result<ShipmentResponseDTO> update(Shipment shipment) {
        Result<ShipmentResponseDTO> result = validateUpdate(shipment);

        if(!result.isSuccess()) {
            return result;
        }

        if(shipment.getShipmentId() <= 0) {
            result.addMessages("SHIPMENT ID MUST BE SET", ResultType.INVALID);
            return result;
        }

        Optional<Shipment> oldShipment = repository.findById(shipment.getShipmentId());
        if(oldShipment.isPresent()) {
            repository.save(shipment);
            return result;
        } else {
            result.addMessages("SHIPMENT " + shipment.getShipmentId() + " NOT FOUND", ResultType.NOT_FOUND);
            return result;
        }
    }

    public Result<ShipmentResponseDTO> validate(Shipment shipment) {
        Result<ShipmentResponseDTO> result = new Result<>();

        if(shipment == null) {
            result.addMessages("SHIPMENT CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(shipment.getOrder() == null || shipment.getOrder().getOrderId() <= 0) {
            result.addMessages("ORDER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Order> order = orderRepository.findById(shipment.getOrder().getOrderId());

        if(order.isEmpty()) {
            result.addMessages("ORDER MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(shipment.getShipmentId() != 0 && (shipment.getTrackingNumber() == null || shipment.getTrackingNumber().isBlank())) {
            result.addMessages("TRACKING NUMBER CANNOT BE NULL OR BLANK", ResultType.INVALID);
        } else if(shipment.getShipmentId() != 0 && shipment.getTrackingNumber().length() != 18) {
            result.addMessages("TRACKING NUMBER MUST BE 18 CHARACTERS", ResultType.INVALID);
        }

        return result;
    }

    public Result<ShipmentResponseDTO> validateUpdate(Shipment shipment) {
        Result<ShipmentResponseDTO> result = new Result<>();

        if(shipment == null) {
            result.addMessages("SHIPMENT CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(shipment.getOrder() == null || shipment.getOrder().getOrderId() <= 0) {
            result.addMessages("ORDER IS REQUIRED", ResultType.INVALID);
            return result;
        }

        Optional<Order> order = orderRepository.findById(shipment.getOrder().getOrderId());

        if(order.isEmpty()) {
            result.addMessages("ORDER MUST EXIST", ResultType.INVALID);
            return result;
        }

        if(shipment.getTrackingNumber() == null || shipment.getTrackingNumber().isBlank()) {
            result.addMessages("TRACKING NUMBER CANNOT BE NULL OR BLANK", ResultType.INVALID);
        } else if(shipment.getTrackingNumber().length() != 18) {
            result.addMessages("TRACKING NUMBER MUST BE 18 CHARACTERS", ResultType.INVALID);
        }

        return result;
    }
}
