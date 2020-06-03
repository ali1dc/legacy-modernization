package com.legacy.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.order.config.Actions;
import com.legacy.order.event.ProductEvent;
import com.legacy.order.model.Product;
import com.legacy.order.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void eventHandler(String data) {

        JsonNode jsonNode;
        ProductEvent event;
        try {
            jsonNode = jsonMapper.readTree(data).at("/payload");
            event = jsonMapper.readValue(jsonNode.toString(), ProductEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

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
            product.setUpdatedBy("me");
            product.setUpdatedDate(new Date());
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
