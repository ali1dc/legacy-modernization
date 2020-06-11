package com.legacy.customer.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CustomerAddressCustomer {

    CustomerAddressDto customerAddress;
    CustomerDto customer;
}
