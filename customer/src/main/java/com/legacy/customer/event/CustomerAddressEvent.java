package com.legacy.customer.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.customer.dto.CustomerAddressDto;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CustomerAddressEvent {
    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    CustomerAddressDto before;

    @JsonProperty("after")
    CustomerAddressDto after;
}
