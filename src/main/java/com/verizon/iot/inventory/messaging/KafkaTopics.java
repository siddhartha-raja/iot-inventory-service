package com.verizon.iot.inventory.messaging;

/** Topic names declared as compile-time constants (style differs from other services on purpose). */
public final class KafkaTopics {

    private KafkaTopics() {
    }

    // consumed
    public static final String ORDER_CREATED = "iot.order.created";
    public static final String ORDER_CANCELLED = "iot.order.cancelled";
    public static final String PROVISIONING_FAILED = "iot.provisioning.failed";
    public static final String SIM_ACTIVATED = "iot.sim.activated";
    public static final String SIM_DEACTIVATED = "iot.sim.deactivated";

    // produced
    public static final String INVENTORY_RESERVED = "iot.inventory.reserved";
    public static final String INVENTORY_RESERVATION_FAILED = "iot.inventory.reservation-failed";

    public static final String CONSUMER_GROUP = "iot-inventory-service-group";
}
