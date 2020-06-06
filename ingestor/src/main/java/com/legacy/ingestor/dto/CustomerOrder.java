package com.legacy.ingestor.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CustomerOrder {

    Customer customer;
    Order order;
    OrderItem orderItem;
}
