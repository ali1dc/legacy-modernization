package com.modernized.product.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modernized.product.model.Product;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEvent {
    @Getter @Setter
    @JsonProperty("op")
    String op;
    @Getter @Setter
    @JsonProperty("ts_ms")
    Timestamp timestamp;
    @Getter @Setter
    @JsonProperty("before")
    Product before;
    @Getter @Setter
    @JsonProperty("after")
    Product after;
}
