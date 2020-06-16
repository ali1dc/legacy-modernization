package com.modernized.product.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class ProductCategoryProduct {

    ProductCategoryDto productCategory;
    ProductDto product;
}
