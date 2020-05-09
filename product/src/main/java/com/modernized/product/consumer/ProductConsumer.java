package com.modernized.product.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modernized.product.model.Product;
import com.modernized.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProductConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private ProductRepository productRepository;

    @KafkaListener(topics = { "legacy.order.products" }, containerFactory = "kafkaListenerContainerFactory")
    public void listenToProducts(@Payload(required = false) String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                 Acknowledgment ack)  {

        try {
            JsonNode jsonNode = jsonMapper.readTree(message).at("/payload");
            Product product = jsonMapper.readValue(jsonNode.toString(), Product.class);
            logger.info("json data: {}", jsonNode);
            logger.info("record saved: {}", product);
            product.setLegacyId(product.getId());
            product.setId(null);
            product.setCreatedBy("system");
            product.setUpdatedBy("system");
            product = productRepository.save(product).block();
            logger.info("record saved: {}", product);
//            ack.acknowledge();
        } catch(DataIntegrityViolationException e) {
            logger.warn("duplicate name detected, do not add it again!");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
