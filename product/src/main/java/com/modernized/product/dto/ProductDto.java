package com.modernized.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    @Getter @Setter
    Integer id;
    @Getter @Setter
    String name;
    @Getter @Setter
    String description;
    @Getter @Setter
    List<String> categories;
    @Getter @Setter
    Float listPrice;
    @Getter @Setter
    Integer quantity;
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
