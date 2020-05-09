package com.modernized.product.model;

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
    @Getter @Setter
    Integer legacyId;
    @JsonProperty("category_name")
    @Getter @Setter
    String name;
    @Getter @Setter
    String description;
    @Getter @Setter
    String createdBy;
    @Getter
    Date createdDate;
    @Getter @Setter
    String updatedBy;
    @Getter
    Date updatedDate;
}
