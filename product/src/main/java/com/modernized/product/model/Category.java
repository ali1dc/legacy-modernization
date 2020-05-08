package com.modernized.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("categories")
public class Category {
    @Id
    @JsonProperty("category_id")
    @Getter @Setter
    Integer id;
    @JsonProperty("category_name")
    @Getter @Setter
    String name;
    String description;
    @JsonIgnore
    @Getter @Setter String createdBy;
    @JsonIgnore
    Date createdDate;
    @JsonIgnore
    @Getter @Setter String updatedBy;
    @JsonIgnore
    Date updatedDate;
}
