package com.legacy.ingestor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("id")
    @Column(name = "product_id")
    Long id;

    @JsonProperty("name")
    @Column(name = "product_name")
    String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @JsonProperty("description")
    String description;

    @JsonProperty("list_price")
    @Column(name = "list_price")
    Float listPrice;

    @JsonProperty("quantity")
    Integer quantity;

    @JsonProperty("created_by")
    @Column(name = "created_by")
    String createdBy;

    @JsonProperty("legacy_id")
    @Transient
    Long legacyId;
}
