package com.modernized.product.dto;

import com.modernized.product.event.ProductCategoryEvent;
import com.modernized.product.event.ProductEvent;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class ProductCategoryProduct {

    ProductCategoryEvent productCategoryEvent;
    ProductEvent productEvent;
}
