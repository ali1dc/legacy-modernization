package com.modernized.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @JsonProperty("category_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @Column(name = "legacy_id")
    Long legacyId;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    Set<ProductCategory> productCategories;

    @JsonProperty("category_name")
    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @JsonProperty("created_by")
    @Column(name = "created_by")
    String createdBy;

    @Column(name = "created_date")
    Date createdDate;

    @Column(name = "updated_by")
    String updatedBy;

    @Column(name = "updated_date")
    Date updatedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.getName());
    }
}
