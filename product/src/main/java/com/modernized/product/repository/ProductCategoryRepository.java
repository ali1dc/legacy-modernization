package com.modernized.product.repository;

import com.modernized.product.model.Category;
import com.modernized.product.model.ProductCategory;
import com.modernized.product.model.ProductCategoryKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductCategoryRepository extends CrudRepository<ProductCategory, ProductCategoryKey> {

    Optional<List<ProductCategory>> findByProductId(Long productId);

//    @Query("SELECT category_id FROM products_categories WHERE product_id = :productID")
//    List<Long> findAllByProductId(Long productID);
//    @Query("select c.name as category from products p\n" +
//            "join products_categories pc ON p.id = pc.product_id\n" +
//            "join categories c ON c.id = pc.category_id\n" +
//            "where p.id = :productID")
//    List<String> findAllCategoryNameByProductId(Long productID);
}
