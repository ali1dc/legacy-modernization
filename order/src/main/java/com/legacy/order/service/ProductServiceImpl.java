package com.legacy.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.order.config.Actions;
import com.legacy.order.event.ProductEvent;
import com.legacy.order.model.Product;
import com.legacy.order.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private ProductRepository productRepository;

    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(ProductEvent event) {

        switch (event.getOp()) {
            case Actions.CREATE:
            case Actions.UPDATE:
            case Actions.READ:
                save(event);
                break;
            case Actions.DELETE:
                delete(event);
                break;
        }
    }

    @Override
    public void save(ProductEvent event) {

        Optional<Product> optionalProduct = productRepository.findById(event.getAfter().getId());
        Product product;
        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
            product.setName(event.getAfter().getName());
            product.setDescription(event.getAfter().getDescription());
            product.setListPrice(event.getAfter().getListPrice());
            product.setQuantity(event.getAfter().getQuantity());
            product.setLegacyId(event.getAfter().getLegacyId());
            product.setUpdatedBy(modCreatedBy);
            product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        } else {
            product = event.getAfter();
        }

        productRepository.save(product);
    }

    @Override
    public void delete(ProductEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
