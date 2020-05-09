package com.modernized.product.config;

import com.modernized.product.consumer.CategoryConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value(value = "${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value(value = "${spring.kafka.consumer.auto-offset-reset}")
    private String offsetConfig;
    @Value(value = "${spring.kafka.consumer.enable-auto-commit}")
    private String enableAutoCommit;
    @Value(value = "${spring.kafka.consumer.auto-commit-interval-ms}")
    private String autoCommitInterval;
    @Value(value = "${spring.kafka.consumer.session-timeout-ms}")
    private String sessionTimeout;


    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.parseBoolean(enableAutoCommit));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetConfig);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, Integer.parseInt(autoCommitInterval));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, Integer.parseInt(sessionTimeout));
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public CategoryConsumer consumer() {
        return new CategoryConsumer();
    }
}
