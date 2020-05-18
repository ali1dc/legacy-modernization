package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Optional<Category> findById(Long id);
    Optional<List<Category>> findByName(String categoryName);
    Optional<Category> findTopByName(String categoryName);
}
