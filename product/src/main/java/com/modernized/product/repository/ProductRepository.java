package com.modernized.product.repository;

import com.modernized.product.model.Category;
import com.modernized.product.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {

    Flux<Product> findAll();
    Mono<Product> findById(Integer id);
    Mono<Product> findTopByName(String name);
}
