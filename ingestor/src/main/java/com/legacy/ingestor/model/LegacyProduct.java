package com.legacy.ingestor.model;

import com.legacy.ingestor.dto.Product;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "products")
public class LegacyProduct {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "product_id")
    Long id;

    @Column(name = "product_name")
    String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    String description;

    @Column(name = "list_price")
    Float listPrice;

    Integer quantity;

    @Column(name = "created_by")
    String createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegacyProduct product = (LegacyProduct) o;
        return Objects.equals(name, product.name) &&
                category.equals(product.category) &&
                Objects.equals(description, product.description) &&
                quantity.equals(product.quantity) &&
                listPrice.equals(product.listPrice);
    }
}
