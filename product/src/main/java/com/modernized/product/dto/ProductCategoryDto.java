package com.modernized.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class ProductCategoryDto {

    Long id;

    @JsonProperty("product_id")
    Long productId;

    @JsonProperty("category_id")
    Long categoryId;
}
