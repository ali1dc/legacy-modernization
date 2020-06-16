package com.legacy.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Product {

    @JsonProperty("id")
    Long id;

    @JsonProperty("name")
    String name;

    @JsonProperty("description")
    String description;

    @JsonProperty("categories")
    List<String> categories;

    @JsonProperty("list_price")
    Float listPrice;

    @JsonProperty("quantity")
    Integer quantity;

    @JsonProperty("created_by")
    String createdBy;

    @JsonProperty("legacy_id")
    Long legacyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id) &&
                Objects.equals(name, product.getName()) &&
                Objects.equals(description, product.description) &&
                listPrice.equals(product.getListPrice()) &&
                quantity.equals(product.quantity) &&
                legacyId.equals(product.getLegacyId());
    }
}
