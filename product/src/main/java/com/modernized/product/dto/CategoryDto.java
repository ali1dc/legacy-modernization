package com.modernized.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CategoryDto {

    Long id;
    String name;
    String description;

    @JsonProperty("legacy_id")
    Long legacyId;

    @JsonProperty("created_by")
    String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    String updatedBy;

    @JsonProperty("updated_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updatedDate;
}
