package com.legacy.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("id")
    Long id;

    @JsonProperty("address_1")
    @Column(name = "address_1")
    String address1;

    @JsonProperty("address_2")
    @Column(name = "address_2")
    String address2;

    @JsonProperty("city")
    String city;

    @JsonProperty("state")
    String state;

    @JsonProperty("zip")
    String zip;

    @JsonIgnore
    @OneToMany(mappedBy = "address")
    Set<CustomerAddress> customerAddresses;

    @JsonProperty("created_by")
    @Column(name = "created_by")

    String createdBy;
    @JsonProperty("created_date")
    @Column(name = "created_date")
    Date createdDate;

    @JsonProperty("updated_by")
    @Column(name = "updated_by")
    String updatedBy;

    @JsonProperty("updated_date")
    @Column(name = "updated_date")
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
