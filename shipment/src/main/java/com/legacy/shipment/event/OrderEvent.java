package com.legacy.shipment.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.shipment.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderEvent {

    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    Order before;

    @JsonProperty("after")
    Order after;
}
