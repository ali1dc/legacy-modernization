package com.modernized.product.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @Column(name = "legacy_id")
    Long legacyId;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    Set<ProductCategory> productCategories;

    @JsonProperty("category_id")
    @Transient
    Long categoryId;

    @JsonProperty("product_name")
    @JsonAlias({"name"})
    @Column(name = "name")
    String name;

    @JsonProperty("description")
    @Column(name = "description")
    String description;

    @JsonProperty("list_price")
    @Column(name = "list_price")
    Float listPrice;

    @JsonProperty("quantity")
    @Column(name = "quantity")
    Integer quantity;

    @JsonProperty("created_by")
    @Column(name = "created_by")
    String createdBy;

    @JsonProperty("created_date")
    @Column(name = "created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    @Column(name = "updated_by")
    String updatedBy;

    @JsonProperty("updated_date")
    @Column(name = "updated_date")
    Date updatedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) &&
                quantity.equals(product.quantity) &&
                listPrice.equals(product.listPrice);
    }
}
