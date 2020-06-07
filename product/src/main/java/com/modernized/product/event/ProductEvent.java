package com.modernized.product.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modernized.product.model.Product;
import lombok.*;

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
