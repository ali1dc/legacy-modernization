package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyProduct;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<LegacyProduct, Long> {

    Optional<LegacyProduct> findById(Long id);
    Optional<LegacyProduct> findTopByProductName(String name);
}
