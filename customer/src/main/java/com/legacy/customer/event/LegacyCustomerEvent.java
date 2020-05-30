package com.legacy.customer.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.customer.model.LegacyCustomer;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LegacyCustomerEvent {

    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    LegacyCustomer before;

    @JsonProperty("after")
    LegacyCustomer after;
}
