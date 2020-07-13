package com.legacy.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("id")
    Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    Order order;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    Product product;

    @Transient
    @JsonProperty("order_id")
    Long orderId;

    @Transient
    @JsonProperty("product_id")
    Long productId;

    @JsonProperty("quantity")
    @Column(name = "quantity")
    Integer quantity;

    @JsonProperty("unit_price")
    @Column(name = "unit_price")
    Float unitPrice;

    @JsonProperty("legacy_id")
    @Column(name = "legacy_id")
    Long legacyId;

    @JsonProperty("legacy_order_id")
    @Column(name = "legacy_order_id")
    Long legacyOrderId;

    @JsonProperty("legacy_product_id")
    @Column(name = "legacy_product_id")
    Long legacyProductId;

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
}
