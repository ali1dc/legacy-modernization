package com.legacy.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Customer {

    @JsonProperty("id")
    Long id;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    @JsonProperty("email")
    String email;

    @JsonProperty("phone")
    String phone;

    @JsonProperty("legacy_id")
    Long legacyId;

    @JsonProperty("created_by")
    String createdBy;

    @JsonProperty("created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    String updatedBy;

    @JsonProperty("updated_date")
    Date updatedDate;

    @Override
    public boolean equals(Object o) {
        Customer customer = (Customer) o;
        return Objects.equals(firstName, customer.firstName)
                && Objects.equals(lastName, customer.lastName)
                && Objects.equals(phone, customer.phone)
                && Objects.equals(email, customer.email);
    }
}
