package com.legacy.ingestor.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class EnrichedCustomer {

    Customer customer;
    List<Address> addresses;
}