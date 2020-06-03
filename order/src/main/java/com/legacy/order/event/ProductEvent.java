package com.legacy.order.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.order.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProductEvent {

    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    Product before;

    @JsonProperty("after")
    Product after;
}

