package com.legacy.shipment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CustomerAddress {

    @JsonProperty("id")
    Long id;

    @JsonProperty("customer_id")
    Long customerId;

    @JsonProperty("address_id")
    Long addressId;

    @JsonProperty("address_type")
    String addressType;

    @JsonProperty("is_default")
    Boolean isDefault;
}
