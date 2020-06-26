package com.legacy.ingestor.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "shipping")
public class LegacyShipment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "shipping_id")
    Long id;

    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    LegacyOrder order;

    @Column(name = "shipping_date")
    Date shippingDate;

    @Column(name = "delivered")
    Boolean delivered;

    @Column(name = "created_by")
    String createdBy;
}
