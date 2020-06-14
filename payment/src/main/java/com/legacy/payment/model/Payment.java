package com.legacy.payment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("payment_id")
    Long id;

    @Transient
    @JsonProperty("customer_id")
    Long customerId;

    @Transient
    @JsonProperty("order_id")
    Long orderId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="customer_id", nullable=false)
    Customer customer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    Customer order;

    @JsonProperty("amount")
    @Column(name = "amount")
    Float amount;

    @JsonProperty("successful")
    @Column(name = "successful")
    Boolean successful;

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
