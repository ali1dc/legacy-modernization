package com.legacy.ingestor.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.model.Product;
import com.legacy.ingestor.repository.CategoryRepository;
import com.legacy.ingestor.repository.ProductRepository;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class ProductStream {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private InteractiveQueryService interactiveQueryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    private final Serde<Category> categorySerde;
    private final Serde<Product> productSerde;

    public ProductStream() {
        this.categorySerde = new JsonSerde<>(Category.class);
        this.productSerde = new JsonSerde<>(Product.class);
    }

    @Bean
    public Function<KStream<String, String>, KStream<Long, Category>> processedCategory() {
        return input -> input
                .map((key, value) -> {
                    Category category = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        category = jsonMapper.readValue(jsonNode.toString(), Category.class);
                        // insert category if not exists om legacy db
                        Optional<Category> legacyCategory = categoryRepository.findTopByName(category.getName());
                        if(!legacyCategory.isPresent()) {
                            category.setCreatedBy(modCreatedBy);
                            categoryRepository.save(category);
                            logger.info("category id: {} - name: {} was inserted to the legacy db.",
                                    category.getId(),
                                    category.getName());
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(category.getId(), category);
                })
                .groupByKey(Grouped.with(Serdes.Long(), categorySerde))
                .reduce((value1, value2) -> value2, Materialized.as("category-store"))
                .toStream();
    }

    @Bean
    public Function<KStream<String, String>, KStream<Long, Product>> processedProduct() {
        return input -> input
                .map((key, value) -> {
                    Product product = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        product = jsonMapper.readValue(jsonNode.toString(), Product.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(product.getId(), product);
                })
                .groupByKey(Grouped.with(Serdes.Long(), productSerde))
                .reduce((value1, value2) -> value2, Materialized.as("product-store"))
                .toStream();
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> legacy() {

        return cs -> cs
                .foreach(((key, value) -> {
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        Long productId = jsonNode.at("/product_id").asLong();
                        Long categoryId = jsonNode.at("/category_id").asLong();
                        logger.info("product id: {} - category id: {}", productId, categoryId);
                        ReadOnlyKeyValueStore<Long, Product> productStore =
                                interactiveQueryService.getQueryableStore("product-store", QueryableStoreTypes.keyValueStore());
                        Product product = productStore.get(productId);
                        // do not add it if the product exists with the same name
                        Optional<Product> productExists = productRepository.findTopByName(product.getName());
                        if(productExists.isPresent()) {
                            logger.info("duplicate product: {} detected, do not insert it again!", product.getName());
                            return;
                        }
//                        // Another way to check if we do not need to insert the record
//                        if (product.getLegacyId() != null) {
//                            logger.info("The event is coming from Legacy and no need to insert it again");
//                            return;
//                        }
                        ReadOnlyKeyValueStore<Long, Category> categoryStore =
                                interactiveQueryService.getQueryableStore("category-store", QueryableStoreTypes.keyValueStore());
                        Category category = categoryStore.get(categoryId);
                        // categories are not matched with ids, so we get it by name
                        Optional<Category> legacyCategory = categoryRepository.findTopByName(category.getName());
                        if(!legacyCategory.isPresent()) {
                            category.setCreatedBy(modCreatedBy);
                            legacyCategory = Optional.of(categoryRepository.save(category));
                        }
                        product.setCategory(legacyCategory.get());
                        product.setId(null);
                        product.setCreatedBy(modCreatedBy);
                        productRepository.save(product);
                        logger.info("Saved: --> id: {} - product: {} - category: {}",
                                product.getId(),
                                product.getName(),
                                product.getCategory().getName());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }));
    }

}
