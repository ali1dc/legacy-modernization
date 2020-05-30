package com.legacy.customer.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.customer.model.CustomerAddress;
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
    CustomerAddress before;

    @JsonProperty("after")
    CustomerAddress after;
}
