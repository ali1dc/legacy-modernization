package com.legacy.ingestor.service;

import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.Product;
import com.legacy.ingestor.events.ProductEvent;
import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.model.LegacyProduct;
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
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public LegacyProduct insert(ProductEvent event) {
        logger.info("inserting the record");
        // do not insert it if the product exists
        Optional<LegacyProduct> productExists = productRepository.findTopByProductName(event.getAfter().getName());
        if (productExists.isPresent()) {
            logger.info("duplicate product detected, do not insert it again!");
            return productExists.get();
        }
        LegacyProduct product = LegacyProduct.builder()
                .productName(event.getAfter().getName())
                .description(event.getAfter().getDescription())
                .listPrice(event.getAfter().getListPrice())
                .quantity(event.getAfter().getQuantity())
                .createdBy(modCreatedBy)
                .build();

        productRepository.save(product);
        return product;
    }

    @Override
    public LegacyProduct insert(Long productId, Long categoryId) {
        logger.info("product id: {} - category id: {}", productId, categoryId);
        ReadOnlyKeyValueStore<Long, Product> productStore =
                interactiveQueryService.getQueryableStore(StateStores.PRODUCT_STORE, QueryableStoreTypes.keyValueStore());
        Product product = productStore.get(productId);
        if (product == null) {
            return null;
        }
        // do not add it if the product exists with the same name
        Optional<LegacyProduct> productExists = productRepository.findTopByProductName(product.getName());
        if (productExists.isPresent()) {
            logger.info("duplicate product: {} detected, do not insert it again!", product.getName());
            return productExists.get();
        }
//        // Another way to check if we do not need to insert the record
//        if (product.getLegacyId() != null) {
//            logger.info("The event is coming from Legacy and no need to insert it again");
//            return;
//        }
        ReadOnlyKeyValueStore<Long, Category> categoryStore =
                interactiveQueryService.getQueryableStore(StateStores.CATEGORY_STORE, QueryableStoreTypes.keyValueStore());
        Category category = categoryStore.get(categoryId);
        // categories are not matched with ids, so we get it by name
        Optional<Category> legacyCategory = categoryRepository.findTopByName(category.getName());
        if (!legacyCategory.isPresent()) {
            category.setCreatedBy(modCreatedBy);
            legacyCategory = Optional.of(categoryRepository.save(category));
        }
        LegacyProduct legacyProduct = LegacyProduct.builder()
                .productName(product.getName())
                .description(product.getDescription())
                .category(legacyCategory.get())
                .listPrice(product.getListPrice())
                .quantity(product.getQuantity())
                .createdBy(modCreatedBy)
                .build();
        productRepository.save(legacyProduct);
        logger.info("Saved: --> id: {} - product: {} - category: {}",
                legacyProduct.getProductId(),
                legacyProduct.getProductName(),
                legacyProduct.getCategory().getName());
        return legacyProduct;
    }

    @Override
    public LegacyProduct update(ProductEvent event) {
        logger.info("updating the record");
        Optional<LegacyProduct> legacyProductOptional = productRepository.findTopByProductName(event.getBefore().getName());
        if (!legacyProductOptional.isPresent()) {
            legacyProductOptional = productRepository.findTopByProductName(event.getAfter().getName());
            return legacyProductOptional.get();
        }
        LegacyProduct legacyProduct = legacyProductOptional.get();
        legacyProduct.setProductName(event.getAfter().getName());
        legacyProduct.setDescription(event.getAfter().getDescription());
        legacyProduct.setListPrice(event.getAfter().getListPrice());
        legacyProduct.setQuantity(event.getAfter().getQuantity());
        legacyProduct.setCreatedBy(modCreatedBy);
        productRepository.save(legacyProduct);
        return legacyProduct;
    }

    @Override
    public void delete(ProductEvent event) {
        logger.info("deleting records; not implemented yet!");
    }
}
