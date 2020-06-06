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
    public void insert(Long productId, Long categoryId) {

        logger.info("product id: {} - category id: {}", productId, categoryId);
        ReadOnlyKeyValueStore<Long, Product> productStore =
                interactiveQueryService.getQueryableStore(StateStores.PRODUCT_STORE, QueryableStoreTypes.keyValueStore());
        Product product = productStore.get(productId);
        if (product == null) return;

        ReadOnlyKeyValueStore<Long, Category> categoryStore =
                interactiveQueryService.getQueryableStore(StateStores.CATEGORY_STORE, QueryableStoreTypes.keyValueStore());
        Category category = categoryStore.get(categoryId);

        // do not add it if the product exists with the same name
        Optional<LegacyProduct> productExists = productRepository.findById(product.getLegacyId());
        Optional<Category> categoryExists = categoryRepository.findById(category.getId());

        if (!productExists.isPresent()) {
            logger.error("do something for missing product; mod id: {}, name: {}", product.getId(), product.getName());
            return;
        }
        if (!categoryExists.isPresent()) {
            logger.error("do something for missing category; mod id: {}, name: {}", category.getId(), category.getName());
        }

        logger.info("Update category products!");
        LegacyProduct legacyProduct = productExists.get();
        legacyProduct.setCategory(categoryExists.orElse(category));
        productRepository.save(legacyProduct);
    }

    @Override
    public LegacyProduct update(ProductEvent event) {

        logger.info("updating the record");
        ReadOnlyKeyValueStore<Long, Product> productStore =
                interactiveQueryService.getQueryableStore(StateStores.PRODUCT_STORE, QueryableStoreTypes.keyValueStore());
        Product product = productStore.get(event.getAfter().getId());
        Optional<LegacyProduct> legacyProductOptional = productRepository.findById(product.getLegacyId());
        if (!legacyProductOptional.isPresent()) {
            legacyProductOptional = productRepository.findTopByProductName(event.getBefore().getName());
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
