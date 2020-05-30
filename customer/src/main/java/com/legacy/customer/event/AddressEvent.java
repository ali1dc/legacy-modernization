package com.legacy.customer.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.customer.model.Address;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AddressEvent {
    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    Address before;

    @JsonProperty("after")
    Address after;
}
