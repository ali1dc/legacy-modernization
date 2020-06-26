package com.legacy.shipment.dto;

import com.legacy.shipment.model.Address;
import com.legacy.shipment.model.Customer;
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
