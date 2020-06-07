package com.modernized.product.repository;

import com.modernized.product.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Iterable<Category> findAll();
    Optional<Category> findById(Long id);
    Optional<Category> findTopByName(String name);
    Optional<Category> findTopByLegacyId(Long legacyId);
}
