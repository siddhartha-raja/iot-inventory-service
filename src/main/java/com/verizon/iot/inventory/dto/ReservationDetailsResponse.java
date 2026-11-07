package com.verizon.iot.inventory.dto;

import java.util.List;

public record ReservationDetailsResponse(String reservationId, String orderId, String status, List<String> iccids) {
}
