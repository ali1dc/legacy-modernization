package com.legacy.customer.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class EnrichedCustomer {

    CustomerDto customer;
    List<AddressDto> addresses;
}
