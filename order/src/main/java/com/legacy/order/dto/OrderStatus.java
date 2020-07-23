package com.legacy.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class OrderStatus {

    @JsonProperty("order_id")
    Long orderId;

    @JsonProperty("legacy_order_id")
    Long legacyOrderId;

    String Status;
}
