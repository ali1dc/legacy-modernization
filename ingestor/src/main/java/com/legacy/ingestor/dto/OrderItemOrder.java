package com.legacy.ingestor.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class OrderItemOrder {

    Order order;
    OrderItem orderItem;
}
