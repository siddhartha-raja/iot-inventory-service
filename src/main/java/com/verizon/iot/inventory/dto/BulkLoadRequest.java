package com.verizon.iot.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BulkLoadRequest(
        @NotBlank String simType,
        @NotBlank String region,
        @Min(1) int count,
        @NotBlank String iccidPrefix) {
}
