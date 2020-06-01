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
@Table(name = "order")
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("id")
    Long id;

    @ManyToOne
    @JoinColumn(name="customer_id", nullable=false)
    Customer customer;

    @JsonProperty("status")
    @Column(name = "status")
    String status;

    @JsonProperty("legacy_id")
    @Column(name = "legacy_id")
    Long legacyId;

    @JsonProperty("created_by")
    @Column(name = "created_by")
    String createdBy;

    @JsonProperty("created_date")
    @Column(name = "created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    @Column(name = "updated_by")
    String updatedBy;

    @JsonProperty("updated_date")
    @Column(name = "updated_date")
    Date updatedDate;
}
