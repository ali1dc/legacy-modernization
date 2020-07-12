package com.modernized.product.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modernized.product.config.Actions;
import com.modernized.product.dto.*;
import com.modernized.product.event.CategoryEvent;
import com.modernized.product.event.ProductCategoryEvent;
import com.modernized.product.event.ProductEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

@Component
public class ProductStream {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired(required = false )
    private ModelMapper mapper;

    private final Serde<ProductDto> productSerde;

    public ProductStream() {
        this.productSerde = new JsonSerde<>(ProductDto.class);
    }

    @Bean
    public Function<KStream<String, ProductEvent>, KStream<String, ProductDto>> pProducts() {

        return input -> input
                .filter((key, event) -> !Objects.equals(event.getOp(), Actions.DELETE))
                .map((key, event) -> {
                    ProductDto product = mapper.map(event.getAfter(), ProductDto.class);
                    return KeyValue.pair(key, product);
                });
    }

    @Bean
    public Function<KStream<String, CategoryEvent>, KStream<String, CategoryDto>> pCategories() {

        return input -> input
                .filter((key, event) -> !Objects.equals(event.getOp(), Actions.DELETE))
                .map((key, event) -> {
                    CategoryDto category = mapper.map(event.getAfter(), CategoryDto.class);
                    return KeyValue.pair(key, category);
                });
    }

    @Bean
    public Function<KStream<String, ProductCategoryEvent>, KStream<String, ProductCategoryDto>> pProductCategories() {

        return input -> input
                .filter((key, event) -> !Objects.equals(event.getOp(), Actions.DELETE))
                .map((key, event) -> {
                    ProductCategoryDto productCategory = mapper.map(event.getAfter(), ProductCategoryDto.class);
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
