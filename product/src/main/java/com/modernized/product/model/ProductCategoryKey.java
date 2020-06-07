package com.modernized.product.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Embeddable
public class ProductCategoryKey implements Serializable {

    @Column(name = "product_id")
    Long productId;

    @Column(name = "category_id")
    Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategoryKey key = (ProductCategoryKey) o;
        return productId.equals(key.productId) &&
                categoryId.equals(key.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }
}
