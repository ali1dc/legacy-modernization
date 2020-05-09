package com.modernized.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    @Getter @Setter
    Integer id;
    @Getter @Setter
    String name;
    @Getter @Setter
    String description;
    @Getter @Setter
    String createdBy;
    @Getter @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createdDate;
    @Getter @Setter
    String updatedBy;
    @Getter @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updatedDate;
}
