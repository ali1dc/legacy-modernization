package com.modernized.product.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modernized.product.config.Actions;
import com.modernized.product.event.ProductEvent;
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
    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @KafkaListener(topics = { "legacy.legacy_order.products" }, containerFactory = "kafkaListenerContainerFactory")
    public void listenToProducts(@Payload(required = false) String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                 Acknowledgment ack)  {

        try {
            JsonNode jsonNode = jsonMapper.readTree(message).at("/payload");
            ProductEvent productEvent = jsonMapper.readValue(jsonNode.toString(), ProductEvent.class);

            if (Objects.equals(productEvent.getOp(), Actions.CREATE)) {
                insertCategory(productEvent);
            } else if (Objects.equals(productEvent.getOp(), Actions.UPDATE)) {
                updateCategory(productEvent);
            }
            else if (Objects.equals(productEvent.getOp(), Actions.DELETE)) {
                deleteCategory(productEvent);
            }

            ack.acknowledge();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void insertCategory(ProductEvent event) {
        logger.info("inserting the record");
        // if record exists skip adding
        Product product = event.getAfter();
        Product productExists = productRepository.findTopByName(product.getName()).block();
        if(productExists != null) {
            logger.info("duplicate product: {} detected, do not insert it again!", product.getName());
            return;
        }
        if(!Objects.equals(modCreatedBy, product.getCreatedBy())) {
            product.setLegacyId(product.getId());
            product.setId(null);
            product.setCreatedBy(legacyCreatedBy);
            product = productRepository.save(product).block();
            // now get category id and set products_category entity
            Category category = categoryRepository.findByLegacyId(product.getCategoryId()).block();
            productCategoryRepository.save(ProductCategoryModel.builder()
                    .productId(product.getId())
                    .categoryId(category.getId())
                    .build()).block();
            logger.info("inserted record: {}", product);
        } else {
            logger.info("event is coming from modernized services" +
                    " and no need to insert it again: {}", product);
        }
    }

    private void updateCategory(ProductEvent event) {
        logger.info("updating the record");
        // find mod side id
        Product existingProduct = productRepository.findTopByName(event.getBefore().getName()).block();
        // set proper values
        existingProduct.setName(event.getAfter().getName());
        existingProduct.setDescription(event.getAfter().getDescription());
        existingProduct.setQuantity(event.getAfter().getQuantity());
        existingProduct.setListPrice(event.getAfter().getListPrice());
        existingProduct.setUpdatedBy(legacyCreatedBy);
        existingProduct.setUpdatedDate(new Date());
        existingProduct = productRepository.save(existingProduct).block();
        Category category = categoryRepository.findByLegacyId(event.getBefore().getCategoryId()).block();
        productCategoryRepository.save(ProductCategoryModel.builder()
                .productId(event.getAfter().getId())
                .categoryId(category.getId())
                .build()).block();
        logger.info("updated record: {}", existingProduct);
    }

    private void deleteCategory(ProductEvent event) {
        logger.info("deleting the record");
    }
}
