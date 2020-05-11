package com.legacy.ingestor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("id")
    @Getter @Setter
    Integer categoryId;
    @JsonProperty("name")
    @Getter @Setter
    String categoryName;
    @Getter @Setter
    @JsonProperty("created_by")
    String createdBy;
}
