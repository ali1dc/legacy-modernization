package com.legacy.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Outbox {

    Long id;

    String type;
    String action;

    @JsonProperty("legacy_id")
    Long legacyId;

    JsonNode payload;

    @JsonProperty("created_by")
    String createBy;

    @JsonProperty("created_date")
    Date createdDate;
}
