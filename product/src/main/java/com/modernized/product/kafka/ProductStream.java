package com.modernized.product.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public Function<KStream<String, ProductCategoryEvent>,
            Function<GlobalKTable<String, ProductEvent>,
                    Function<GlobalKTable<String, CategoryEvent>,
                            KStream<String, ProductDto>>>> enrichedProducts() {
        return productCategories -> (
                products -> (
                        categories -> (
                                productCategories.leftJoin(products,
                                        (productCategoryId, productCategory) -> productCategory.getAfter().getProductId().toString(),
                                        ProductCategoryProduct::new)
                                        .leftJoin(categories,
                                                (id, productCategoryProduct) -> productCategoryProduct.getProductCategoryEvent().getAfter().getCategoryId().toString(),
                                                (productCategoryProduct, category) -> {
                                                    ArrayList<String> catNames = new ArrayList<>();
                                                    ProductDto dto = mapper.map(productCategoryProduct.getProductEvent().getAfter(), ProductDto.class);
                                                    if (dto == null) {
                                                        dto = new ProductDto();
                                                    }
                                                    if (category != null) {
                                                        catNames.add(category.getAfter().getName());
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
