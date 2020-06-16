package com.legacy.ingestor.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.Actions;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.Product;
import com.legacy.ingestor.events.CategoryEvent;
import com.legacy.ingestor.events.ProductEvent;
import com.legacy.ingestor.model.Category;
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
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    private final Serde<Category> categorySerde;
    private final Serde<Product> productSerde;

    public ProductStream() {
        this.categorySerde = new JsonSerde<>(Category.class);
        this.productSerde = new JsonSerde<>(Product.class);
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> iProducts() {

        return products -> products

                .map((key, value) -> {
                    Product product = null;
                    try {
                        product = jsonMapper.readValue(
                                jsonMapper.readTree(value).toString(), Product.class);

                        productService.save(product);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert product != null;
                    return KeyValue.pair(key, product);
                })
                .groupByKey(Grouped.with(Serdes.String(), productSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.PRODUCT_STORE));
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> iProductUpdate() {

        return products -> products
                .filter((key, value) -> {
                    ProductEvent event = null;
                    try {
                        event = jsonMapper.readValue(
                                jsonMapper.readTree(value).toString(), ProductEvent.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert event != null;
                    return Objects.equals(event.getOp(), Actions.UPDATE);
                })
                .foreach((key, value) -> {
                    ProductEvent event = null;
                    try {
                        event = jsonMapper.readValue(
                                jsonMapper.readTree(value).toString(), ProductEvent.class);

                        productService.update(event);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
    }

   @Bean
    public Function<KStream<String, String>, KStream<Long, Category>> iCategory() {

        return input -> input
                .filter((key, value) -> {
                    CategoryEvent event = null;
                    try {
                        event = jsonMapper.readValue(
                                jsonMapper.readTree(value).toString(), CategoryEvent.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert event != null;
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .map((key, value) -> {
                    Category category = null;
                    try {
                        CategoryEvent event = jsonMapper.readValue(
                                jsonMapper.readTree(value).toString(), CategoryEvent.class);

                        switch (event.getOp()) {
                            case Actions.CREATE:
                            case Actions.READ:
                                category = categoryService.insert(event);
                                break;
                            case Actions.UPDATE:
                                category = categoryService.update(event);
                                break;
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert category != null;
                    return KeyValue.pair(category.getId(), category);
                })
                .groupByKey(Grouped.with(Serdes.Long(), categorySerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.CATEGORY_STORE))
                .toStream();
    }


    /**

     @Bean
    public Function<KStream<String, String>, KStream<Long, Product>> iProduct() {
        return input -> input
                .filter((key, value) -> {
                    ProductEvent event = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/payload");
                        event = jsonMapper.readValue(jsonNode.toString(), ProductEvent.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert event != null;
                    return !Objects.equals(event.getOp(), Actions.DELETE);
                })
                .map((key, value) -> {
                    Product product = null;
                    try {
                        ProductEvent event = jsonMapper.readValue(
                                jsonMapper.readTree(value).toString(), ProductEvent.class);
                        product = event.getAfter();
                        LegacyProduct legacyProduct = null;

                        switch (event.getOp()) {
                            case Actions.CREATE:
                            case Actions.READ:
                                legacyProduct = productService.insert(event);
                                break;
                            case Actions.UPDATE:
                                legacyProduct = productService.update(event);
                                break;
                        }
                        assert legacyProduct != null;
                        product.setLegacyId(legacyProduct.getProductId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert product != null;
                    return KeyValue.pair(product.getId(), product);
                })
                .groupByKey(Grouped.with(Serdes.Long(), productSerde))
                .reduce((value1, value2) -> value2, Materialized.as(StateStores.PRODUCT_STORE))
                .toStream();
    }

    */
}
