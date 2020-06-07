package com.modernized.product.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modernized.product.model.Category;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CategoryEvent {

    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    Category before;

    @JsonProperty("after")
    Category after;
}
