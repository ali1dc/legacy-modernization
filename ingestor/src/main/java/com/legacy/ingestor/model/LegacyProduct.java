package com.legacy.ingestor.model;

import lombok.*;

import javax.persistence.*;

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
    Long productId;

    @Column(name = "product_name")
    String productName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    String description;

    @Column(name = "list_price")
    Float listPrice;

    Integer quantity;

    @Column(name = "created_by")
    String createdBy;
}
