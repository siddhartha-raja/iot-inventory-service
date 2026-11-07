package com.verizon.iot.inventory.service;

import com.verizon.iot.inventory.dto.AvailabilityResponse;
import com.verizon.iot.inventory.dto.BulkLoadRequest;
import com.verizon.iot.inventory.dto.ReservationDetailsResponse;
import com.verizon.iot.inventory.entity.ReservationStatus;
import com.verizon.iot.inventory.entity.SimCard;
import com.verizon.iot.inventory.entity.SimReservation;
import com.verizon.iot.inventory.entity.SimStatus;
import com.verizon.iot.inventory.events.InventoryReservationFailedEvent;
import com.verizon.iot.inventory.events.InventoryReservedEvent;
import com.verizon.iot.inventory.events.OrderCreatedMessage;
import com.verizon.iot.inventory.exception.ReservationNotFoundException;
import com.verizon.iot.inventory.messaging.InventoryEventPublisher;
import com.verizon.iot.inventory.repository.SimCardRepository;
import com.verizon.iot.inventory.repository.SimReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final SimCardRepository simCardRepository;
    private final SimReservationRepository reservationRepository;
    private final InventoryEventPublisher eventPublisher;

    @Value("${app.inventory.low-stock-threshold:500}")
    private int lowStockThreshold;

    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(String simType, int quantity, String region) {
        long count = simCardRepository.countBySimTypeAndRegionAndStatus(simType, region, SimStatus.AVAILABLE);
        return new AvailabilityResponse(simType, region, (int) count, count >= quantity);
    }

    /** Reserve stock for an order and emit either inventory.reserved or inventory.reservation-failed. */
    @Transactional
    public void reserveForOrder(OrderCreatedMessage msg) {
        List<SimCard> sims = simCardRepository.findAvailable(msg.simType(), msg.region(), PageRequest.of(0, msg.quantity()));
        if (sims.size() < msg.quantity()) {
            eventPublisher.publishReservationFailed(new InventoryReservationFailedEvent(
                    msg.orderId(), "Requested " + msg.quantity() + " but only " + sims.size() + " available"));
            return;
        }

        String reservationId = "RSV-" + UUID.randomUUID();
        reservationRepository.save(SimReservation.builder()
                .reservationId(reservationId)
                .orderId(msg.orderId())
                .customerId(msg.customerId())
                .accountNumber(msg.accountNumber())
                .ratePlanCode(msg.ratePlanCode())
                .simType(msg.simType())
                .quantity(msg.quantity())
                .status(ReservationStatus.ACTIVE)
                .createdAt(Instant.now())
                .build());

        for (SimCard sim : sims) {
            sim.setStatus(SimStatus.RESERVED);
            sim.setReservationId(reservationId);
            sim.setOrderId(msg.orderId());
            sim.setUpdatedAt(Instant.now());
        }
        simCardRepository.saveAll(sims);

        long remaining = simCardRepository.countBySimTypeAndRegionAndStatus(msg.simType(), msg.region(), SimStatus.AVAILABLE);
        if (remaining < lowStockThreshold) {
            log.warn("Low stock simType={} region={} remaining={}", msg.simType(), msg.region(), remaining);
        }

        eventPublisher.publishReserved(new InventoryReservedEvent(
                msg.orderId(), reservationId, msg.customerId(), msg.accountNumber(),
                msg.simType(), msg.quantity(), msg.ratePlanCode(), Instant.now()));
    }

    @Transactional
    public void releaseByOrder(String orderId, String reason) {
        reservationRepository.findByOrderId(orderId).ifPresent(r -> releaseReservation(r, reason));
    }

    @Transactional
    public void releaseByReservationId(String reservationId, String reason) {
        SimReservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        releaseReservation(r, reason);
    }

    private void releaseReservation(SimReservation reservation, String reason) {
        if (reservation.getStatus() == ReservationStatus.RELEASED) {
            return;
        }
        List<SimCard> sims = simCardRepository.findByReservationId(reservation.getReservationId());
        sims.forEach(s -> {
            s.setStatus(SimStatus.AVAILABLE);
            s.setReservationId(null);
            s.setOrderId(null);
            s.setUpdatedAt(Instant.now());
        });
        simCardRepository.saveAll(sims);
        reservation.setStatus(ReservationStatus.RELEASED);
        reservationRepository.save(reservation);
        log.info("Released reservation={} reason={}", reservation.getReservationId(), reason);
    }

    @Transactional
    public void markSimsActive(String reservationId, List<String> iccids) {
        simCardRepository.findAllById(iccids).forEach(s -> {
            s.setStatus(SimStatus.ACTIVE);
            s.setUpdatedAt(Instant.now());
            simCardRepository.save(s);
        });
        reservationRepository.findById(reservationId).ifPresent(r -> {
            r.setStatus(ReservationStatus.CONSUMED);
            reservationRepository.save(r);
        });
    }

    @Transactional
    public void markSimsRetired(List<String> iccids) {
        simCardRepository.findAllById(iccids).forEach(s -> {
            s.setStatus(SimStatus.RETIRED);
            s.setUpdatedAt(Instant.now());
            simCardRepository.save(s);
        });
    }

    @Transactional(readOnly = true)
    public ReservationDetailsResponse getReservation(String reservationId) {
        SimReservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        List<String> iccids = simCardRepository.findByReservationId(reservationId).stream()
                .map(SimCard::getIccid).toList();
        return new ReservationDetailsResponse(r.getReservationId(), r.getOrderId(), r.getStatus().name(), iccids);
    }

    @Transactional
    public int bulkLoad(BulkLoadRequest request) {
        List<SimCard> batch = new ArrayList<>();
        for (int i = 0; i < request.count(); i++) {
            String iccid = request.iccidPrefix() + String.format("%010d", i);
            batch.add(SimCard.builder()
                    .iccid(iccid)
                    .imsi("31048" + String.format("%010d", i))
                    .simType(request.simType())
                    .region(request.region())
                    .status(SimStatus.AVAILABLE)
                    .updatedAt(Instant.now())
                    .build());
        }
        simCardRepository.saveAll(batch);
        return batch.size();
    }
}
