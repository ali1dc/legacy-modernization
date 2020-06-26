package com.legacy.shipment.model;

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
@Table(name = "shippings")
public class Shipment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("shipping_id")
    Long id;

    @Transient
    @JsonProperty("order_id")
    Long orderId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    Order order;

    Boolean delivered;

    @JsonProperty("shipping_date")
    @Column(name = "shipping_date")
    Date shippingDate;

    @JsonProperty("legacy_id")
    @Column(name = "legacy_id")
    Long legacyId;

    @JsonProperty("legacy_order_id")
    @Column(name = "legacy_order_id")
    Long legacyOrderId;

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
