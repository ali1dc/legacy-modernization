package com.modernized.product.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("products")
public class Product {
    @Id
    @JsonProperty("product_id")
    @Getter @Setter
    Integer id;
    @Getter @Setter
    Integer legacyId;
    @Getter @Setter
    @JsonProperty("category_id")
    @Transient
    Integer categoryId;
    @JsonProperty("product_name")
    @Getter @Setter
    String name;
    @Getter @Setter
    String description;
    @Getter @Setter
    @JsonProperty("list_price")
    Float listPrice;
    @Getter @Setter
    Integer quantity;
    @Getter @Setter
    @JsonProperty("created_by")
    String createdBy;
    @Getter @Setter
    Date createdDate;
    @Getter @Setter
    String updatedBy;
    @Getter @Setter
    Date updatedDate;
}
