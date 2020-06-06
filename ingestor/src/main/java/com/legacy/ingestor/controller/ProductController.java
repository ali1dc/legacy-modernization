package com.legacy.ingestor.controller;

import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.model.LegacyProduct;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @GetMapping("/categories")
    public List<Category> categories() {
        List<Category> categories = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, Category> store =
                interactiveQueryService.getQueryableStore("category-store", QueryableStoreTypes.keyValueStore());
        KeyValueIterator<Long, Category> all = store.all();
        while (all.hasNext()) {
            KeyValue<Long, Category> value = all.next();
            categories.add(value.value);
        }
        return categories;
    }

    @GetMapping("/categories/{id}")
    public Category categoryById(@PathVariable Long id) {
        List<Category> categories = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, Category> store =
                interactiveQueryService.getQueryableStore("category-store", QueryableStoreTypes.keyValueStore());
        return store.get(id);
    }

    @GetMapping("/products")
    public List<LegacyProduct> products() {
        List<LegacyProduct> legacyProducts = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, LegacyProduct> store =
                interactiveQueryService.getQueryableStore("product-store", QueryableStoreTypes.keyValueStore());
        KeyValueIterator<Long, LegacyProduct> all = store.all();
        while (all.hasNext()) {
            KeyValue<Long, LegacyProduct> value = all.next();
            legacyProducts.add(value.value);
        }
        return legacyProducts;
    }

    @GetMapping("/products/{id}")
    public LegacyProduct productById(@PathVariable Long id) {
        ReadOnlyKeyValueStore<Long, LegacyProduct> store =
                interactiveQueryService.getQueryableStore("product-store", QueryableStoreTypes.keyValueStore());
        return store.get(id);
    }
}
