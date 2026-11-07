package com.verizon.iot.inventory.events;

/** Published to iot.inventory.reservation-failed. */
public record InventoryReservationFailedEvent(String orderId, String reason) {
}
