package com.legacy.ingestor.service;

import com.legacy.ingestor.config.LegacyIdTopics;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.Product;
import com.legacy.ingestor.events.ProductEvent;
import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.model.LegacyProduct;
import com.legacy.ingestor.repository.CategoryRepository;
import com.legacy.ingestor.repository.ProductRepository;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
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
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired(required = false )
    private ModelMapper mapper;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public LegacyProduct insert(ProductEvent event) {

        logger.info("inserting the record");
        // do not insert it if the product exists
        Optional<LegacyProduct> productExists = productRepository.findTopByName(event.getAfter().getName());
        if (productExists.isPresent()) {
            logger.info("duplicate product detected, do not insert it again!");
            LegacyProduct product = productExists.get();
            kafkaTemplate.send(LegacyIdTopics.PRODUCT,
                    event.getAfter().getId().toString(),
                    product.getId().toString());
            return product;
        }
        LegacyProduct product = LegacyProduct.builder()
                .name(event.getAfter().getName())
                .description(event.getAfter().getDescription())
                .listPrice(event.getAfter().getListPrice())
                .quantity(event.getAfter().getQuantity())
                .createdBy(modCreatedBy)
                .build();

        productRepository.save(product);
        kafkaTemplate.send(LegacyIdTopics.PRODUCT,
                event.getAfter().getId().toString(),
                product.getId().toString());
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
        Optional<Category> categoryExists = categoryRepository.findTopByName(category.getName());

        if (!productExists.isPresent()) {
            logger.error("do something for missing product; mod id: {}, name: {}", product.getId(), product.getName());
            return;
        }
        if (!categoryExists.isPresent()) {
            logger.error("do something for missing category; mod id: {}, name: {}", category.getId(), category.getName());
        }

        logger.info("Update category products!");
        LegacyProduct legacyProduct = productExists.get();
        categoryExists.ifPresent(legacyProduct::setCategory);
        productRepository.save(legacyProduct);
    }

    @Override
    public void update(ProductEvent event) {

        logger.info("updating the record");
        Optional<LegacyProduct> legacyProductOptional = productRepository.findById(event.getAfter().getLegacyId());
        if (!legacyProductOptional.isPresent()) {
            legacyProductOptional = productRepository.findTopByName(event.getBefore().getName());
        }
        legacyProductOptional.ifPresent(product-> {
            product.setName(event.getAfter().getName());
            product.setDescription(event.getAfter().getDescription());
            product.setListPrice(event.getAfter().getListPrice());
            product.setQuantity(event.getAfter().getQuantity());
            product.setCreatedBy(modCreatedBy);
            productRepository.save(product);
        });
    }

//    @Override
//    public LegacyProduct update(ProductEvent event) {
//
//        logger.info("updating the record");
//        ReadOnlyKeyValueStore<Long, Product> productStore =
//                interactiveQueryService.getQueryableStore(StateStores.PRODUCT_STORE, QueryableStoreTypes.keyValueStore());
//        Product product = productStore.get(event.getAfter().getId());
//        Optional<LegacyProduct> legacyProductOptional = productRepository.findById(product.getLegacyId());
//        if (!legacyProductOptional.isPresent()) {
//            legacyProductOptional = productRepository.findTopByName(event.getBefore().getName());
//        }
//        LegacyProduct legacyProduct = legacyProductOptional.get();
//        legacyProduct.setName(event.getAfter().getName());
//        legacyProduct.setDescription(event.getAfter().getDescription());
//        legacyProduct.setListPrice(event.getAfter().getListPrice());
//        legacyProduct.setQuantity(event.getAfter().getQuantity());
//        legacyProduct.setCreatedBy(modCreatedBy);
//        productRepository.save(legacyProduct);
//        return legacyProduct;
//    }

    @Override
    public void delete(ProductEvent event) {
        logger.info("deleting records; not implemented yet!");
    }

    @Override
    public void save(Product product) {

        Category category = getCategory(product.getCategories().get(0));

        Optional<LegacyProduct> productExists = productRepository.findTopByName(product.getName());
        if (productExists.isPresent()) {
            logger.info("product exists!");
            LegacyProduct legacyProduct = productExists.get();
            LegacyProduct newLegacyProduct = mapper.map(product, LegacyProduct.class);
            newLegacyProduct.setCategory(category);
            if (!newLegacyProduct.equals(legacyProduct)) {
                logger.info(" product update is necessary!");
                newLegacyProduct.setId(legacyProduct.getId());
                productRepository.save(legacyProduct);
            }
        } else {
            logger.info("insert a product!");
            LegacyProduct legacyProduct = mapper.map(product, LegacyProduct.class);
            legacyProduct.setCategory(category);
            legacyProduct.setCreatedBy(modCreatedBy);
            legacyProduct.setId(null);
            productRepository.save(legacyProduct);

            kafkaTemplate.send(LegacyIdTopics.PRODUCT,
                    product.getId().toString(),
                    legacyProduct.getId().toString());
        }
    }

    private Category getCategory(String name) {

        Optional<Category> optionalCategory = categoryRepository.findTopByName(name);
        Category category;
        if (!optionalCategory.isPresent()) {
            category = Category.builder()
                    .name(name)
                    .createdBy(modCreatedBy)
                    .build();
            categoryRepository.save(category);
            logger.info("category inserted: id: {}, name: {}!",category.getId(), category.getName());
        } else {
            category = optionalCategory.get();
        }

        return category;
    }
}
