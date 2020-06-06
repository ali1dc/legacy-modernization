package com.legacy.ingestor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("id")
    @Column(name = "category_id")
    Long id;

    @JsonProperty("name")
    @Column(name = "category_name")
    String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<LegacyProduct> legacyProducts;

    @JsonProperty("created_by")
    @Column(name = "created_by")
    String createdBy;
}
