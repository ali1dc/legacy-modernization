package com.modernized.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @JsonProperty("product_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
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

    @Column(name = "created_date")
    Date createdDate;

    @Column(name = "updated_by")
    String updatedBy;

    @Column(name = "updated_date")
    Date updatedDate;
}
