package com.verizon.iot.inventory.messaging;

import com.verizon.iot.inventory.events.OrderCancelledMessage;
import com.verizon.iot.inventory.events.OrderCreatedMessage;
import com.verizon.iot.inventory.events.ProvisioningFailedMessage;
import com.verizon.iot.inventory.events.SimActivatedMessage;
import com.verizon.iot.inventory.events.SimDeactivatedMessage;
import com.verizon.iot.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final InventoryService inventoryService;

    /** Step 2 of the flow: reserve SIMs for a freshly created order. */
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = KafkaTopics.CONSUMER_GROUP)
    public void onOrderCreated(OrderCreatedMessage message) {
        log.info("Received order.created orderId={}", message.orderId());
        inventoryService.reserveForOrder(message);
    }

    /** Customer cancelled before/after activation - give SIMs back. */
    @KafkaListener(topics = KafkaTopics.ORDER_CANCELLED, groupId = KafkaTopics.CONSUMER_GROUP)
    public void onOrderCancelled(OrderCancelledMessage message) {
        log.info("Received order.cancelled orderId={}", message.orderId());
        inventoryService.releaseByOrder(message.orderId(), message.reason());
    }

    /** Saga compensation: provisioning failed so reservation must be released. */
    @KafkaListener(topics = KafkaTopics.PROVISIONING_FAILED, groupId = KafkaTopics.CONSUMER_GROUP)
    public void onProvisioningFailed(ProvisioningFailedMessage message) {
        log.warn("Compensating for provisioning failure orderId={}", message.orderId());
        inventoryService.releaseByOrder(message.orderId(), "Provisioning failed: " + message.reason());
    }

    @KafkaListener(topics = KafkaTopics.SIM_ACTIVATED, groupId = KafkaTopics.CONSUMER_GROUP)
    public void onSimActivated(SimActivatedMessage message) {
        inventoryService.markSimsActive(message.reservationId(), message.iccids());
    }

    @KafkaListener(topics = KafkaTopics.SIM_DEACTIVATED, groupId = KafkaTopics.CONSUMER_GROUP)
    public void onSimDeactivated(SimDeactivatedMessage message) {
        inventoryService.markSimsRetired(message.iccids());
    }
}
