package com.verizon.iot.inventory.events;

import java.time.Instant;

/** Consumed from iot.order.cancelled. */
public record OrderCancelledMessage(String orderId, String reason, Instant cancelledAt) {
}
