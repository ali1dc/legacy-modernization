package com.modernized.product.repository;

import com.modernized.product.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface ProductRepository extends CrudRepository<Product, Long> {

    Iterable<Product> findAll();
    Optional<Product> findById(Integer id);
    Optional<Product> findTopByName(String name);
}
