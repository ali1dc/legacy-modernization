package com.legacy.ingestor.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.ingestor.dto.Customer;
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
