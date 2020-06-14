package com.legacy.payment.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class EnrichedCustomer {

    CustomerAddress customerAddress;
    Customer customer;
    Address address;
}
