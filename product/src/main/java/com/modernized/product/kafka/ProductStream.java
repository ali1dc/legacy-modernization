package com.modernized.product.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modernized.product.dto.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Function;

@Component
public class ProductStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;

    private final Serde<ProductDto> productSerde;

    public ProductStream() {
        this.productSerde = new JsonSerde<>(ProductDto.class);
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, ProductDto>> pProducts() {

        return input -> input
                .map((key, value) -> {
                    ProductDto product = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        product = jsonMapper.readValue(jsonNode.toString(), ProductDto.class);
                        logger.info("Key: {}, Value: {}", key, product.getId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, product);
                });
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, CategoryDto>> pCategories() {

        return input -> input
                .map((key, value) -> {
                    CategoryDto category = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        category = jsonMapper.readValue(jsonNode.toString(), CategoryDto.class);
                        logger.info("Key: {}, Value: {}", key, category.getId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, category);
                });
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, ProductCategoryDto>> pProductCategories() {

        return input -> input
                .map((key, value) -> {
                    ProductCategoryDto productCategory = null;
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(value).at("/after");
                        productCategory = jsonMapper.readValue(jsonNode.toString(), ProductCategoryDto.class);
                        logger.info("Key: {}, Value: {}", key, productCategory.getId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return KeyValue.pair(key, productCategory);
                });
    }

    @Bean
    public Function<KStream<String, ProductCategoryDto>,
            Function<GlobalKTable<String, ProductDto>,
                    Function<GlobalKTable<String, CategoryDto>,
                            KStream<String, ProductDto>>>> enrichedProducts() {
        return productCategories -> (
                products -> (
                        categories -> (
                                productCategories.leftJoin(products,
                                        (productCategoryId, productCategory) -> productCategory.getProductId().toString(),
                                        ProductCategoryProduct::new)
                                        .leftJoin(categories,
                                                (id, productCategoryProduct) -> productCategoryProduct.getProductCategory().getCategoryId().toString(),
                                                (productCategoryProduct, category) -> {
                                                    ArrayList<String> catNames = new ArrayList<>();
                                                    ProductDto dto = productCategoryProduct.getProduct();
                                                    if (dto == null) {
                                                        dto = new ProductDto();
                                                    }
                                                    if (category != null) {
                                                        catNames.add(category.getName());
                                                        dto.setCategories(catNames);
                                                    }
                                                    return dto;
                                        })
                        )
                        .map((key, value) -> KeyValue.pair(value.getId().toString(), value))
                        .groupByKey(Grouped.with(Serdes.String(), productSerde))
                        .reduce((value1, value2) -> {
                            value1.getCategories().forEach(cat -> {
                                if (!value2.getCategories().contains(cat)) {
                                    value2.getCategories().add(cat);
                                }
                            });
                            return value2;
                        })
                        .toStream()
                )
        );
    }
}
