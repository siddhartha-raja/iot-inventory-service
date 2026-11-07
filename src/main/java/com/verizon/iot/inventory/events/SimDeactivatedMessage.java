package com.verizon.iot.inventory.events;

import java.time.Instant;
import java.util.List;

/** Consumed from iot.sim.deactivated. */
public record SimDeactivatedMessage(String orderId, List<String> iccids, String reason, Instant deactivatedAt) {
}
