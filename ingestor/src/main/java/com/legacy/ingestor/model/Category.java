package com.legacy.ingestor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

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
    @Column(name = "category_id")
    Long id;
    @JsonProperty("name")
    @Getter @Setter
    @Column(name = "category_name")
    String name;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Getter @Setter
    @JsonIgnore
    Set<Product> products;
    @Getter @Setter
    @JsonProperty("created_by")
    @Column(name = "created_by")
    String createdBy;
}
