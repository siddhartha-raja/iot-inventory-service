package com.verizon.iot.inventory.dto;

public record AvailabilityResponse(String simType, String region, int availableCount, boolean available) {
}
