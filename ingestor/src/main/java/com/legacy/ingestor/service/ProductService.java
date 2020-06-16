package com.legacy.ingestor.service;

import com.legacy.ingestor.dto.Product;
import com.legacy.ingestor.events.ProductEvent;
import com.legacy.ingestor.model.LegacyProduct;

public interface ProductService {
    LegacyProduct insert(ProductEvent event);
    void insert(Long productId, Long categoryId);
    void update(ProductEvent event);
    void delete(ProductEvent event);
    void save(Product product);
}
