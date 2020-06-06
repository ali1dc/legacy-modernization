package com.legacy.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

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

    @JsonProperty("list_price")
    Float listPrice;

    @JsonProperty("quantity")
    Integer quantity;

    @JsonProperty("created_by")
    String createdBy;

    @JsonProperty("legacy_id")
    Long legacyId;
}
