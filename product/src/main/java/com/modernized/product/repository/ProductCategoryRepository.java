package com.modernized.product.repository;

import com.modernized.product.model.ProductCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductCategoryRepository extends CrudRepository<ProductCategory, Long> {

    Optional<List<ProductCategory>> findByProductId(Long productId);
}
