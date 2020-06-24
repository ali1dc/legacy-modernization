package com.legacy.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class OrderItem {

    @JsonProperty("id")
    Long id;

    @JsonProperty("order_id")
    Long orderId;

    @JsonProperty("product_id")
    Long productId;

    @JsonProperty("quantity")
    Integer quantity;

    @JsonProperty("unit_price")
    Float unitPrice;

    @JsonProperty("legacy_id")
    Long legacyId;

    @JsonProperty("legacy_order_id")
    Long legacyOrderId;

    @JsonProperty("legacy_product_id")
    Long legacyProductId;

    @JsonProperty("created_by")
    String createdBy;

    @JsonProperty("created_date")
    Date createdDate;
}
