package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.LegacyProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<LegacyProduct, Long> {

    Optional<LegacyProduct> findById(Long id);
    Optional<LegacyProduct> findTopByName(String name);
    @Query(value = "SELECT TOP 1 id, product_name, description, list_price, quantity, category_id, created_by " +
            "FROM products ORDER BY NEWID()", nativeQuery = true)
    Optional<LegacyProduct> findRandom();
}
