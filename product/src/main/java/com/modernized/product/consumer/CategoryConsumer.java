package com.modernized.product.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modernized.product.config.Actions;
import com.modernized.product.event.CategoryEvent;
import com.modernized.product.model.Category;
import com.modernized.product.repository.CategoryRepository;
import javafx.scene.chart.CategoryAxis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Objects;

@Component
public class CategoryConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

//    @KafkaListener(
//            topicPartitions = @TopicPartition(topic = "legacy.order.categories",
//                    partitionOffsets = {
//                            @PartitionOffset(partition = "0", initialOffset = "0")
////                            @PartitionOffset(partition = "3", initialOffset = "0")
//                    }))
//    public void listenToCategories(
//            @Payload String message,
//            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws JsonProcessingException {

    @KafkaListener(topics = { "legacy.legacy_order.categories" }, containerFactory = "kafkaListenerContainerFactory")
    public void listenToCategories(@Payload(required = false) String message,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                   Acknowledgment ack)  {

        try {
            JsonNode jsonNode = jsonMapper.readTree(message).at("/payload");
            CategoryEvent categoryEvent = jsonMapper.readValue(jsonNode.toString(), CategoryEvent.class);

            if (Objects.equals(categoryEvent.getOp(), Actions.CREATE)) {
                insertCategory(categoryEvent);
            } else if (Objects.equals(categoryEvent.getOp(), Actions.UPDATE)) {
                updateCategory(categoryEvent);
            }
            else if (Objects.equals(categoryEvent.getOp(), Actions.DELETE)) {
                deleteCategory(categoryEvent);
            }

//            ack.acknowledge();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private Category insertCategory(CategoryEvent event) {
        logger.info("inserting the record");
        // if record exists skip adding
        Category category = event.getAfter();

        Category categoryExists = categoryRepository.findTopByName(category.getName()).block();
        if(categoryExists != null) {
            logger.info("duplicate category: {} detected, do not add it again!", category.getName());
            return categoryExists;
        }
        if(!Objects.equals(modCreatedBy, category.getCreatedBy())) {
            category.setLegacyId(category.getId());
            category.setId(null);
            category.setCreatedBy(legacyCreatedBy);
            category = categoryRepository.save(category).block();
            logger.info("record saved: {}", category);
        } else {
            logger.info("event is coming from modernized services" +
                    " and no need to insert it again: {}", category);
        }

        return category;
    }

    private Category updateCategory(CategoryEvent event) {
        logger.info("updating the record");
        // find mod side id
        Category existingCategory = categoryRepository.findTopByName(event.getBefore().getName()).block();
        // set proper values
        existingCategory.setName(event.getAfter().getName());
        existingCategory.setUpdatedBy(legacyCreatedBy);
        existingCategory.setUpdatedDate(new Date());
        existingCategory = categoryRepository.save(existingCategory).block();
        return existingCategory;
    }

    private void deleteCategory(CategoryEvent event) {
        logger.info("deleting records; not implemented yet!");
    }
}
