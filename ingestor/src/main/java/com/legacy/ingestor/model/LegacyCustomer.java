package com.legacy.ingestor.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "customers")
public class LegacyCustomer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "billing_address1")
    String billingAddress1;

    @Column(name = "billing_address2")
    String billingAddress2;

    @Column(name = "billing_city")
    String billingCity;

    @Column(name = "billing_state")
    String billingState;

    @Column(name = "billing_zip")
    String billingZip;

    @Column(name = "shipping_address1")
    String shippingAddress1;

    @Column(name = "shipping_address2")
    String shippingAddress2;

    @Column(name = "shipping_city")
    String shippingCity;

    @Column(name = "shipping_state")
    String shippingState;

    @Column(name = "shipping_zip")
    String shippingZip;

    @Column(name = "email")
    String email;

    @Column(name = "phone")
    String phone;

    @Column(name = "created_by")
    String createdBy;
}
