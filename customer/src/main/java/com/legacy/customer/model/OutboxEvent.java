package com.legacy.customer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "events")
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    String type;
    String action;
    Boolean processed;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    JsonNode payload;

    @JsonProperty("created_by")
    @Column(name = "created_by")
    String createBy;

    @JsonProperty("created_date")
    @Column(name = "created_date")
    Date createdDate;
}
