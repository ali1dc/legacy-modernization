package com.legacy.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LegacyCustomer {

    @JsonProperty("id")
    Long id;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    @JsonProperty("billing_address1")
    String billingAddress1;

    @JsonProperty("billing_address2")
    String billingAddress2;

    @JsonProperty("billing_city")
    String billingCity;

    @JsonProperty("billing_state")
    String billingState;

    @JsonProperty("billing_zip")
    String billingZip;

    @JsonProperty("shipping_address1")
    String shippingAddress1;

    @JsonProperty("shipping_address2")
    String shippingAddress2;

    @JsonProperty("shipping_city")
    String shippingCity;

    @JsonProperty("shipping_state")
    String shippingState;

    @JsonProperty("shipping_zip")
    String shippingZip;

    @JsonProperty("email")
    String email;

    @JsonProperty("phone")
    String phone;

    @JsonProperty("created_by")
    String createdBy;
}
