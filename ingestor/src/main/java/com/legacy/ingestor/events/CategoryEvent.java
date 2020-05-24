package com.legacy.ingestor.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy.ingestor.model.Category;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEvent {
    @Getter @Setter
    @JsonProperty("op")
    String op;
    @Getter @Setter
    @JsonProperty("ts_ms")
    Timestamp timestamp;
    @Getter @Setter
    @JsonProperty("before")
    Category before;
    @Getter @Setter
    @JsonProperty("after")
    Category after;
}
