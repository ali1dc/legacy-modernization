package com.legacy.customer.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.customer.model.Customer;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CustomerEvent {

    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    Customer before;

    @JsonProperty("after")
    Customer after;
}
