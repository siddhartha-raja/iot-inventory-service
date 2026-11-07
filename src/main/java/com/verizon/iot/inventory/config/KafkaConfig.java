package com.verizon.iot.inventory.config;

import com.verizon.iot.inventory.messaging.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic inventoryReservedTopic() {
        return TopicBuilder.name(KafkaTopics.INVENTORY_RESERVED).partitions(6).replicas(3).build();
    }

    @Bean
    public NewTopic inventoryReservationFailedTopic() {
        return TopicBuilder.name(KafkaTopics.INVENTORY_RESERVATION_FAILED).partitions(6).replicas(3).build();
    }

    @Bean
    public RecordMessageConverter jsonMessageConverter() {
        return new StringJsonMessageConverter();
    }

    /** Poison messages go to <topic>.DLT after 3 retries. */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 3L));
    }
}
