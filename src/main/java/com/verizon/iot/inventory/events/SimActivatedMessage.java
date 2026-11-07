package com.verizon.iot.inventory.events;

import java.time.Instant;
import java.util.List;

/** Consumed from iot.sim.activated. */
public record SimActivatedMessage(
        String orderId,
        String reservationId,
        String customerId,
        String accountNumber,
        String ratePlanCode,
        List<String> iccids,
        Instant activatedAt) {
}
