package com.verizon.iot.inventory.messaging;

import com.verizon.iot.inventory.events.InventoryReservationFailedEvent;
import com.verizon.iot.inventory.events.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishReserved(InventoryReservedEvent event) {
        log.info("Publishing inventory.reserved orderId={} reservationId={}", event.orderId(), event.reservationId());
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event.orderId(), event);
    }

    public void publishReservationFailed(InventoryReservationFailedEvent event) {
        log.warn("Publishing inventory.reservation-failed orderId={}", event.orderId());
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVATION_FAILED, event.orderId(), event);
    }
}
