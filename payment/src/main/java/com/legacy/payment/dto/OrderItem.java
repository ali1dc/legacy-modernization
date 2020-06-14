package com.legacy.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
}
