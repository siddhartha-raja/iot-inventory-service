package com.verizon.iot.inventory.repository;

import com.verizon.iot.inventory.entity.SimReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimReservationRepository extends JpaRepository<SimReservation, String> {
    Optional<SimReservation> findByOrderId(String orderId);
}
