package com.modernized.product.repository;

import com.modernized.product.model.ProductCategoryModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductCategoryRepository extends ReactiveCrudRepository<ProductCategoryModel, Integer> {

    @Query("SELECT category_id FROM products_categories WHERE product_id = :productID")
    Flux<Integer> findAllByProductId(Integer productID);
    @Query("select c.name as category from products p\n" +
            "join products_categories pc ON p.id = pc.product_id\n" +
            "join categories c ON c.id = pc.category_id\n" +
            "where p.id = :productID")
    Flux<String> findAllCategoryNameByProductId(Integer productID);
}
