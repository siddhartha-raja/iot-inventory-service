package com.verizon.iot.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "sim_reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimReservation {

    @Id
    private String reservationId;
    private String orderId;
    private String customerId;
    private String accountNumber;
    private String ratePlanCode;
    private String simType;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Instant createdAt;
}
