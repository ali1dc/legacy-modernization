package com.legacy.ingestor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("id")
    @Getter @Setter
    @Column(name = "product_id")
    Long id;
    @Getter @Setter
    @JsonProperty("name")
    @Column(name = "product_name")
    String name;
    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    @Getter @Setter
    @JsonProperty("description")
    String description;
    @Getter @Setter
    @JsonProperty("list_price")
    @Column(name = "list_price")
    Float listPrice;
    @Getter @Setter
    @JsonProperty("quantity")
    Integer quantity;
    @JsonProperty("created_by")
    @Getter @Setter
    @Column(name = "created_by")
    String createdBy;
    @JsonProperty("legacy_id")
    @Getter @Setter
    @Transient
    Long legacyId;
}
