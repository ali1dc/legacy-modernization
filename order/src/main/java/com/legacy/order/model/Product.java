package com.legacy.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @JsonProperty("id")
    Long id;

    @JsonProperty("legacy_id")
    Long legacyId;

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

    @JsonProperty("created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    String updatedBy;

    @JsonProperty("updated_date")
    Date updatedDate;
}
