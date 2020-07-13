package com.legacy.customer.dto;

import com.legacy.customer.event.CustomerAddressEvent;
import com.legacy.customer.event.CustomerEvent;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CustomerAddressCustomer {

    CustomerAddressEvent customerAddressEvent;
    CustomerEvent customerEvent;
}
