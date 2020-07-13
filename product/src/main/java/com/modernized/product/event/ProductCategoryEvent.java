package com.modernized.product.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modernized.product.dto.ProductCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProductCategoryEvent {

    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    ProductCategoryDto before;

    @JsonProperty("after")
    ProductCategoryDto after;
}
