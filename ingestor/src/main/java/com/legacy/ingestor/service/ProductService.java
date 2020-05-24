package com.legacy.ingestor.service;

import com.legacy.ingestor.events.ProductEvent;
import com.legacy.ingestor.model.Product;

public interface ProductService {
    Product insert(ProductEvent event);
    Product insert(Long productId, Long categoryId);
    Product update(ProductEvent event);
    void delete(ProductEvent event);
}
