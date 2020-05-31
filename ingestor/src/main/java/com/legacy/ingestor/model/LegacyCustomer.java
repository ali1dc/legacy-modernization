package com.legacy.ingestor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    @Column(name = "customer_id")
    Long id;

    @JsonProperty("first_name")
    @Column(name = "first_name")
    String firstName;

    @JsonProperty("last_name")
    @Column(name = "last_name")
    String lastName;

    @JsonProperty("billing_address1")
    @Column(name = "billing_address1")
    String billingAddress1;

    @JsonProperty("billing_address2")
    @Column(name = "billing_address2")
    String billingAddress2;

    @JsonProperty("billing_city")
    @Column(name = "billing_city")
    String billingCity;

    @JsonProperty("billing_state")
    @Column(name = "billing_state")
    String billingState;

    @JsonProperty("billing_zip")
    @Column(name = "billing_zip")
    String billingZip;

    @JsonProperty("shipping_address1")
    @Column(name = "shipping_address1")
    String shippingAddress1;

    @JsonProperty("shipping_address2")
    @Column(name = "shipping_address2")
    String shippingAddress2;

    @JsonProperty("shipping_city")
    @Column(name = "shipping_city")
    String shippingCity;

    @JsonProperty("shipping_state")
    @Column(name = "shipping_state")
    String shippingState;

    @JsonProperty("shipping_zip")
    @Column(name = "shipping_zip")
    String shippingZip;

    @JsonProperty("email")
    @Column(name = "email")
    String email;

    @JsonProperty("phone")
    @Column(name = "phone")
    String phone;

    @JsonProperty("created_by")
    @Column(name = "created_by")
    String createdBy;
}
