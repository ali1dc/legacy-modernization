package com.legacy.ingestor.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.events.CategoryEvent;
import com.legacy.ingestor.events.ProductEvent;
import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.model.Product;
import com.legacy.ingestor.repository.CategoryRepository;
import com.legacy.ingestor.repository.ProductRepository;
import com.legacy.ingestor.service.CategoryService;
import com.legacy.ingestor.service.ProductService;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.Objects;
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
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

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
                .filter((key, value) -> {
                    CategoryEvent event = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        event = jsonMapper.readValue(jsonNode.toString(), CategoryEvent.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .map((key, value) -> {
                    Category category = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        CategoryEvent event = jsonMapper.readValue(jsonNode.toString(), CategoryEvent.class);
                        if (Objects.equals(event.getOp(), Actions.CREATE)) {
                            category = categoryService.insert(event);
                        } else if (Objects.equals(event.getOp(), Actions.READ)) {
                            // for snapshots
                            category = categoryService.insert(event);
                        } else if (Objects.equals(event.getOp(), Actions.UPDATE)) {
                            category = categoryService.update(event);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(category.getId(), category);
                })
                .groupByKey(Grouped.with(Serdes.Long(), categorySerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.CATEGORY_STORE))
                .toStream();
    }

    @Bean
    public Function<KStream<String, String>, KStream<Long, Product>> processedProduct() {
        return input -> input
                .filter((key, value) -> {
                    ProductEvent event = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        event = jsonMapper.readValue(jsonNode.toString(), ProductEvent.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .map((key, value) -> {
                    Product product = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        ProductEvent event = jsonMapper.readValue(jsonNode.toString(), ProductEvent.class);
                        product = event.getAfter();
                        if (Objects.equals(event.getOp(), Actions.READ)) {
                            product = productService.insert(event);
                        } else if (Objects.equals(event.getOp(), Actions.UPDATE)) {
                            product = productService.update(event);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(product.getId(), product);
                })
                .groupByKey(Grouped.with(Serdes.Long(), productSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.PRODUCT_STORE))
                .toStream();
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> legacy() {

        return cs -> cs
                .foreach(((key, value) -> {
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload/after");
                        Long productId = jsonNode.at("/product_id").asLong();
                        Long categoryId = jsonNode.at("/category_id").asLong();
                        productService.insert(productId, categoryId);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }));
    }
}
