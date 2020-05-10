package com.modernized.product.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modernized.product.model.Category;
import com.modernized.product.repository.CategoryRepository;
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

@Component
public class CategoryConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CategoryRepository categoryRepository;

//    @KafkaListener(
//            topicPartitions = @TopicPartition(topic = "legacy.order.categories",
//                    partitionOffsets = {
//                            @PartitionOffset(partition = "0", initialOffset = "0")
////                            @PartitionOffset(partition = "3", initialOffset = "0")
//                    }))
//    public void listenToCategories(
//            @Payload String message,
//            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws JsonProcessingException {

    @KafkaListener(topics = { "legacy.order.categories" }, containerFactory = "kafkaListenerContainerFactory")
    public void listenToCategories(@Payload(required = false) String message,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                   Acknowledgment ack)  {

        try {
            JsonNode jsonNode = jsonMapper.readTree(message).at("/payload");
            Category category = jsonMapper.readValue(jsonNode.toString(), Category.class);
            category.setLegacyId(category.getId());
            category.setId(null);
            category.setCreatedBy("system");
            category.setUpdatedBy("system");
            categoryRepository.save(category).subscribe();
            logger.info("record saved: {}", category);
            ack.acknowledge();
        } catch(DataIntegrityViolationException e) {
            logger.info("duplicate name detected, do not add it again!");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
