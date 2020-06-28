package com.modernized.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.modernized.product.config.Actions;
import com.modernized.product.event.ProductEvent;
import com.modernized.product.model.Category;
import com.modernized.product.model.Product;
import com.modernized.product.model.ProductCategory;
import com.modernized.product.repository.CategoryRepository;
import com.modernized.product.repository.ProductCategoryRepository;
import com.modernized.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ObjectMapper jsonMapper;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void productHandler(String data) {

        try {
            JsonNode jsonNode = jsonMapper.readTree(data).at("/payload");
            ProductEvent event = jsonMapper.readValue(jsonNode.toString(), ProductEvent.class);
            switch (event.getOp()) {
                case Actions.CREATE:
                case Actions.READ:
                    insert(event);
                    break;
                case Actions.UPDATE:
                    update(event);
                    break;
                case Actions.DELETE:
                    delete(event);
                    break;
            }
        } catch (MismatchedInputException me) {
            logger.warn(me.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(ProductEvent event) {

        Product product = event.getAfter();
        if(Objects.equals(product.getCreatedBy(), modCreatedBy)) {
            logger.info("The message is coming from mod; nothing to do!");
            return;
        }

        Optional<Product> productExists = productRepository.findTopByName(product.getName());
        if(!productExists.isPresent()) {
            product.setLegacyId(product.getId());
            product.setId(null);
            product.setCreatedBy(legacyCreatedBy);
            product.setCreatedDate(event.getTimestamp());
            productRepository.save(product);
            // now get category id and set products_category entity
            Optional<Category> category = categoryRepository.findTopByLegacyId(product.getCategoryId());
            category.ifPresent(cat -> productCategoryRepository.save(ProductCategory.builder()
                    .product(product)
                    .category(cat)
                    .build()));
            logger.info("inserted record: {}", product);
        }
    }

    @Override
    public void update(ProductEvent event) {

        logger.info("updating the record");
        Optional<Product> existingProduct = productRepository.findTopByName(event.getBefore().getName());

        existingProduct.ifPresent(product -> {
            if (!event.getAfter().equals(product)) {
                product.setName(event.getAfter().getName());
                product.setDescription(event.getAfter().getDescription());
                product.setQuantity(event.getAfter().getQuantity());
                product.setListPrice(event.getAfter().getListPrice());
                product.setUpdatedBy(legacyCreatedBy);
                product.setUpdatedDate(event.getTimestamp());
                productRepository.save(product);
                Optional<Category> optionalCategory = categoryRepository.findTopByLegacyId(event.getBefore().getCategoryId());
                optionalCategory.ifPresent(cat -> productCategoryRepository.save(ProductCategory.builder()
                        .product(product)
                        .category(cat)
                        .build()));
            }
        });
    }

    @Override
    public void update(Long id, Long legacyId) {

        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(product -> {
            if (product.getLegacyId() == null) {
                product.setLegacyId(legacyId);
                product.setUpdatedBy(legacyCreatedBy);
                product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                productRepository.save(product);
            }
        });
    }

    @Override
    public void delete(ProductEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
