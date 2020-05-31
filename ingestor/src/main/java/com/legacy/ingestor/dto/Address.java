package com.legacy.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Address {

    @JsonProperty("id")
    Long id;

    @JsonProperty("address_1")
    String address1;

    @JsonProperty("address_2")
    String address2;

    @JsonProperty("city")
    String city;

    @JsonProperty("state")
    String state;

    @JsonProperty("zip")
    String zip;

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
        Address address = (Address) o;
        return Objects.equals(address1, address.address1)
                && Objects.equals(address2, address.address2)
                && Objects.equals(city, address.city)
                && Objects.equals(state, address.state)
                && Objects.equals(zip, address.zip);
    }
}
