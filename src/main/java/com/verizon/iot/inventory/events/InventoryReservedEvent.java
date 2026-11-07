package com.verizon.iot.inventory.events;

import java.time.Instant;

/** Published to iot.inventory.reserved. */
public record InventoryReservedEvent(
        String orderId,
        String reservationId,
        String customerId,
        String accountNumber,
        String simType,
        int quantity,
        String ratePlanCode,
        Instant reservedAt) {
}
