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
    @JsonProperty("created_by")
    String createdBy;
    @Getter
    Date createdDate;
    @JsonProperty("updated_by")
    @Getter @Setter
    String updatedBy;
    @Getter
    Date updatedDate;
}
