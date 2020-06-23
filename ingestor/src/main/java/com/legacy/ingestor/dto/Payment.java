package com.legacy.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Payment {

    @JsonProperty("id")
    Long id;

    @JsonProperty("customer_id")
    Long customerId;

    @JsonProperty("order_id")
    Long orderId;

    @JsonProperty("amount")
    Float amount;

    @JsonProperty("successful")
    Boolean successful;

    @JsonProperty("legacy_id")
    Long legacyId;

    @JsonProperty("legacy_order_id")
    Long legacyOrderId;

    @JsonProperty("created_by")
    String createdBy;

    @JsonProperty("created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    String updatedBy;

    @JsonProperty("updated_date")
    Date updatedDate;
}
