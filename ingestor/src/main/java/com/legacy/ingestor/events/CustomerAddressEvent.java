package com.legacy.ingestor.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.ingestor.dto.CustomerAddress;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
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
