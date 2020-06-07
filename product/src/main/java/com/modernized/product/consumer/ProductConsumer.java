package com.modernized.product.consumer;

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
    public java.util.function.Consumer<KStream<String, String>> categories() {

        return input -> input
                .foreach((key, value) -> {
                    categoryService.categoryHandler(value);
                });
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> products() {

        return input -> input
                .foreach((key, value) -> {
                    productService.productHandler(value);
                });
    }
}
