package com.legacy.customer.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class EnrichedCustomer {

    CustomerAddressDto customerAddress;
    CustomerDto customer;
    AddressDto address;
}
