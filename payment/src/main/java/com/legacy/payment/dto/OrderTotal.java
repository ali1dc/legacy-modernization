package com.legacy.payment.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class OrderTotal {

    Long id;
    Float total;
}
