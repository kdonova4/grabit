package com.kdonova4.grabit.model;

import com.kdonova4.grabit.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreateDTO {
    private int orderId;
}
