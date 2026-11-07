package com.verizon.iot.inventory.controller;

import com.verizon.iot.inventory.dto.AvailabilityResponse;
import com.verizon.iot.inventory.dto.BulkLoadRequest;
import com.verizon.iot.inventory.dto.ReservationDetailsResponse;
import com.verizon.iot.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "SIM Inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    /** Called synchronously by iot-order-service before an order is accepted. Exposed via ingress. */
    @GetMapping("/sims/availability")
    @Operation(summary = "Check available SIM stock")
    public AvailabilityResponse availability(@RequestParam String simType,
                                             @RequestParam int quantity,
                                             @RequestParam String region) {
        return inventoryService.checkAvailability(simType, quantity, region);
    }

    @PostMapping("/sims/bulk-load")
    @Operation(summary = "Load a batch of SIMs into stock (admin)")
    public Map<String, Integer> bulkLoad(@Valid @RequestBody BulkLoadRequest request) {
        return Map.of("loaded", inventoryService.bulkLoad(request));
    }

    /** Called synchronously by iot-provisioning-service. NOT exposed via ingress (cluster-internal). */
    @GetMapping("/reservations/{reservationId}")
    @Operation(summary = "Get reserved ICCIDs for a reservation")
    public ReservationDetailsResponse getReservation(@PathVariable String reservationId) {
        return inventoryService.getReservation(reservationId);
    }

    @PostMapping("/reservations/{reservationId}/release")
    @Operation(summary = "Manually release a reservation (ops)")
    public Map<String, String> release(@PathVariable String reservationId,
                                       @RequestParam(defaultValue = "manual release") String reason) {
        inventoryService.releaseByReservationId(reservationId, reason);
        return Map.of("reservationId", reservationId, "status", "RELEASED");
    }
}
