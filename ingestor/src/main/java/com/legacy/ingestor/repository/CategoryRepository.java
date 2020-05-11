package com.legacy.ingestor.repository;

import com.legacy.ingestor.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    Optional<Category> findById(Integer id);
}
