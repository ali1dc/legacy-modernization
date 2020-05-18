package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findById(Long id);
}
