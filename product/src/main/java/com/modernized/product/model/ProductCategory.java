package com.modernized.product.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "products_categories")
public class ProductCategory {

    @EmbeddedId
    ProductCategoryKey id;

    @ManyToOne
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne
    @MapsId("category_id")
    @JoinColumn(name = "category_id")
    Category category;
}
