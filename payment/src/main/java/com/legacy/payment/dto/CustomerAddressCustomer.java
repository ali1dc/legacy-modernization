package com.legacy.payment.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CustomerAddressCustomer {

    CustomerAddress customerAddress;
    Customer customer;
}
