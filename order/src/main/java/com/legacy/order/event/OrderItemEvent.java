package com.legacy.order.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.order.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderItemEvent {

    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    OrderItem before;

    @JsonProperty("after")
    OrderItem after;
}
