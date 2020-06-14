package com.legacy.payment.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.payment.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PaymentEvent {

    @JsonProperty("op")
    String op;

    @JsonProperty("ts_ms")
    Timestamp timestamp;

    @JsonProperty("before")
    Payment before;

    @JsonProperty("after")
    Payment after;
}
