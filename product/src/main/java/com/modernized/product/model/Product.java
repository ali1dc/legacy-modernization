package com.modernized.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.kafka.connect.data.Decimal;
import org.springframework.data.annotation.Id;
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
    @JsonProperty("product_name")
    @Getter @Setter
    String name;
    @Getter @Setter
    String description;
    @Getter @Setter
    Decimal listPrice;
    @Getter @Setter
    Integer quantity;
    @Getter @Setter
    String createdBy;
    @Getter @Setter
    Date createdDate;
    @Getter @Setter
    String updatedBy;
    @Getter @Setter
    Date updatedDate;
}
