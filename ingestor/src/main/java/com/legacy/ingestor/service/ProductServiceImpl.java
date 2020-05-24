package com.legacy.ingestor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.events.ProductEvent;
import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.model.Product;
import com.legacy.ingestor.repository.CategoryRepository;
import com.legacy.ingestor.repository.ProductRepository;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public Product insert(ProductEvent event) {
        logger.info("inserting the record");
        // do not insert it if the product exists
        Optional<Product> productExists = productRepository.findTopByName(event.getAfter().getName());
        if(productExists.isPresent()) {
            logger.info("duplicate product detected, do not insert it again!");
            return productExists.get();
        }
        Product product = event.getAfter();
        product.setCreatedBy(modCreatedBy);
        product.setId(null);
        productRepository.save(product);
        return product;
    }

    @Override
    public Product insert(Long productId, Long categoryId) {
        logger.info("product id: {} - category id: {}", productId, categoryId);
        ReadOnlyKeyValueStore<Long, Product> productStore =
                interactiveQueryService.getQueryableStore("product-store", QueryableStoreTypes.keyValueStore());
        Product product = productStore.get(productId);
        // do not add it if the product exists with the same name
        Optional<Product> productExists = productRepository.findTopByName(product.getName());
        if(productExists.isPresent()) {
            logger.info("duplicate product: {} detected, do not insert it again!", product.getName());
            return productExists.get();
        }
//        // Another way to check if we do not need to insert the record
//        if (product.getLegacyId() != null) {
//            logger.info("The event is coming from Legacy and no need to insert it again");
//            return;
//        }
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
        return product;
    }

    @Override
    public Product update(ProductEvent event) {
        logger.info("updating the record");
        Optional<Product> legacyProductOptional = productRepository.findTopByName(event.getBefore().getName());
        if (!legacyProductOptional.isPresent()) {
            legacyProductOptional = productRepository.findTopByName(event.getAfter().getName());
            return legacyProductOptional.get();
        }
        Product legacyProduct = legacyProductOptional.get();
        legacyProduct.setName(event.getAfter().getName());
        legacyProduct.setDescription(event.getAfter().getDescription());
        legacyProduct.setListPrice(event.getAfter().getListPrice());
        legacyProduct.setQuantity(event.getAfter().getQuantity());
        productRepository.save(legacyProduct);
        return legacyProduct;
    }

    @Override
    public void delete(ProductEvent event) {
        logger.info("deleting records; not implemented yet!");
    }
}
