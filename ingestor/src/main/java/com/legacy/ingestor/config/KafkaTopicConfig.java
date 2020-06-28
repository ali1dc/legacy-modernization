package com.legacy.ingestor.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic category() {
        return new NewTopic(KafkaTopics.CATEGORY, 1, (short) 1);
    }

    @Bean
    public NewTopic product() {
        return new NewTopic(KafkaTopics.PRODUCT, 1, (short) 1);
    }

    @Bean
    public NewTopic customer() {
        return new NewTopic(KafkaTopics.CUSTOMER, 1, (short) 1);
    }

    @Bean
    public NewTopic order() {
        return new NewTopic(KafkaTopics.ORDER, 1, (short) 1);
    }

    @Bean
    public NewTopic orderItem() {
        return new NewTopic(KafkaTopics.ORDER_ITEM, 1, (short) 1);
    }

    @Bean
    public NewTopic payment() {
        return new NewTopic(KafkaTopics.PAYMENT, 1, (short) 1);
    }

    @Bean
    public NewTopic shipment() {
        return new NewTopic(KafkaTopics.SHIPPING, 1, (short) 1);
    }

    @Bean
    public NewTopic orderStatus() {
        return new NewTopic(KafkaTopics.ORDER_STATUS_TOPIC, 1, (short) 1);
    }
}
