package com.modernized.product.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modernized.product.model.Category;
import com.modernized.product.model.Product;
import com.modernized.product.model.ProductCategoryModel;
import com.modernized.product.repository.CategoryRepository;
import com.modernized.product.repository.ProductCategoryRepository;
import com.modernized.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ProductConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Value(value = "${legacy.mod.created-by}")
    private String legacyModCreatedBy;

    @KafkaListener(topics = { "legacy.order.products" }, containerFactory = "kafkaListenerContainerFactory")
    public void listenToProducts(@Payload(required = false) String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                 Acknowledgment ack)  {

        try {
            JsonNode jsonNode = jsonMapper.readTree(message).at("/payload");
            Product product = jsonMapper.readValue(jsonNode.toString(), Product.class);
            if(!Objects.equals(legacyModCreatedBy, product.getCreatedBy())) {
                product.setLegacyId(product.getId());
                product.setId(null);
                product.setCreatedBy("system");
                product.setUpdatedBy("system");
                product = productRepository.save(product).block();
                // now get category id and set products_category entity
                Category category = categoryRepository.findByLegacyId(product.getCategoryId()).block();
                productCategoryRepository.save(ProductCategoryModel.builder()
                        .productId(product.getId())
                        .categoryId(category.getId())
                        .build()).block();
                logger.info("record saved: {}", product);
            } else {
                logger.info("event is coming from modernized services" +
                        " and no need to insert it again: {}", product);
            }
            ack.acknowledge();
        } catch(DataIntegrityViolationException e) {
            logger.warn("duplicate name detected, do not add it again!");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
