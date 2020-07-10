package com.legacy.ingestor.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "payments")
public class LegacyPayment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "payment_id")
    Long id;

    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    LegacyOrder order;

    @Column(name = "charged_amount")
    Float amount;

    @Column(name = "successful")
    Boolean successful;

    @Column(name = "created_by")
    String createdBy;
}
