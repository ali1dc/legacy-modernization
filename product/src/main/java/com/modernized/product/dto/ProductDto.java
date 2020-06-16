package com.modernized.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class ProductDto {

    Long id;
    String name;
    String description;
    List<String> categories;

    @JsonProperty("list_price")
    Float listPrice;
    Integer quantity;

    @JsonProperty("legacy_id")
    Long legacyId;

    @JsonProperty("created_by")
    String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    String updatedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_date")
    Date updatedDate;
}
