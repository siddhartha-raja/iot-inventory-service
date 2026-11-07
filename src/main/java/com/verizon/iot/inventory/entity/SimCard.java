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
@Table(name = "sim_cards")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimCard {

    @Id
    private String iccid;
    private String imsi;
    private String simType;
    private String region;

    @Enumerated(EnumType.STRING)
    private SimStatus status;

    private String reservationId;
    private String orderId;
    private Instant updatedAt;
}
