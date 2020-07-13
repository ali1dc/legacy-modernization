package com.modernized.product.kafka;

import com.modernized.product.event.CategoryEvent;
import com.modernized.product.event.ProductEvent;
import com.modernized.product.service.CategoryService;
import com.modernized.product.service.ProductService;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ProductConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @Bean
    public java.util.function.Consumer<KStream<String, CategoryEvent>> categories() {

        return input -> input.foreach((key, event) -> categoryService.categoryHandler(event));
    }

    @Bean
    public java.util.function.Consumer<KStream<String, ProductEvent>> products() {

        return input -> input.foreach((key, event) -> productService.productHandler(event));
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> categoryLegacyIds() {

        return input -> input
                .foreach((key, value) -> {
                    Long id = Long.parseLong(key);
                    Long legacyId = Long.parseLong(value);
                    categoryService.update(id, legacyId);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> productLegacyIds() {

        return input -> input
                .foreach((key, value) -> {
                    Long id = Long.parseLong(key);
                    Long legacyId = Long.parseLong(value);
                    productService.update(id, legacyId);
                });
    }

}
