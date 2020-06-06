package com.legacy.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Order {

    @JsonProperty("id")
    Long id;

    @JsonProperty("customer_id")
    Long customerId;

    @JsonProperty("status")
    String status;

    @JsonProperty("legacy_id")
    Long legacyId;

    @JsonProperty("created_by")
    String createdBy;

    @JsonProperty("created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    String updatedByBy;

    @JsonProperty("updated_date")
    Date updatedDate;
}
