package com.verizon.iot.inventory.config;

import com.verizon.iot.inventory.dto.BulkLoadRequest;
import com.verizon.iot.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
@RequiredArgsConstructor
public class SeedDataLoader implements CommandLineRunner {

    private final InventoryService inventoryService;

    @Override
    public void run(String... args) {
        inventoryService.bulkLoad(new BulkLoadRequest("PHYSICAL_4FF", "US-EAST", 2000, "8914800000"));
        inventoryService.bulkLoad(new BulkLoadRequest("ESIM", "US-WEST", 1000, "8914810000"));
    }
}
