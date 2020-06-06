package com.legacy.ingestor.service;

import com.legacy.ingestor.events.ProductEvent;
import com.legacy.ingestor.model.LegacyProduct;

public interface ProductService {
    LegacyProduct insert(ProductEvent event);
    LegacyProduct insert(Long productId, Long categoryId);
    LegacyProduct update(ProductEvent event);
    void delete(ProductEvent event);
}
