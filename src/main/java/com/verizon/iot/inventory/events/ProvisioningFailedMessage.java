package com.verizon.iot.inventory.events;

import java.time.Instant;

/** Consumed from iot.provisioning.failed - triggers compensation (release SIMs). */
public record ProvisioningFailedMessage(String orderId, String reservationId, String reason, Instant failedAt) {
}
