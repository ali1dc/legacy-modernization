package com.legacy.ingestor.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "order_items")
public class LegacyOrderItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "item_id")
    Long itemId;

    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    LegacyOrder order;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    LegacyProduct product;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "unit_price")
    Float unitPrice;

    @Column(name = "created_by")
    String createdBy;
}
