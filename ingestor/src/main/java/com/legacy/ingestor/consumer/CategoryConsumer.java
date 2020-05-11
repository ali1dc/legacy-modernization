package com.legacy.ingestor.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @KafkaListener(topics = { "product.public.categories" }, containerFactory = "kafkaListenerContainerFactory")
    public void listenToCategories(@Payload(required = false) String message,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                   Acknowledgment ack)  {

        try {
            JsonNode jsonNode = jsonMapper.readTree(message).at("/payload");
            Category category = jsonMapper.readValue(jsonNode.toString(), Category.class);
            int legacyId = jsonNode.at("/legacy_id").asInt();
            if(legacyId == 0) {
                category.setCategoryId(null);
                category.setCreatedBy("modernized");
                categoryRepository.save(category);
                logger.info("saved category: {}, {}, {}",
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getCreatedBy());
            } else {
                logger.info("we do not need to insert category: {}, {}, {}",
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getCreatedBy());
            }
            ack.acknowledge();
        } catch(DataIntegrityViolationException e) {
            logger.info("duplicate name detected, do not add it again!");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
