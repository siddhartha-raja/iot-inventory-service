package com.verizon.iot.inventory.events;

import java.time.Instant;

/** Consumed from iot.order.created (produced by iot-order-service). */
public record OrderCreatedMessage(
        String eventId,
        String orderId,
        String customerId,
        String accountNumber,
        String simType,
        int quantity,
        String region,
        String ratePlanCode,
        Instant createdAt) {
}
