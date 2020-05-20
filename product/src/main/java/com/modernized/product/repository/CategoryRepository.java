package com.modernized.product.repository;

import com.modernized.product.model.Category;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Integer> {

    Flux<Category> findAll();
    Mono<Category> findById(Integer id);
    Mono<Category> findTopByName(String name);
    @Query("SELECT * FROM categories WHERE legacy_id = :legacyId")
    Mono<Category> findByLegacyId(Integer legacyId);
}
