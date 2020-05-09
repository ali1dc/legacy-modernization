package com.modernized.product.repository;

import com.modernized.product.model.ProductCategoryModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends ReactiveCrudRepository<ProductCategoryModel, Long> {
}
