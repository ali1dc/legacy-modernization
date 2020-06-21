package com.legacy.ingestor.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class OrderCustomer {

    Long id;
    Order order;
    Customer customer;
}
