package com.legacy.payment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @JsonProperty("id")
    Long id;

    @JsonProperty("first_name")
    @Column(name = "first_name")
    String firstName;

    @JsonProperty("last_name")
    @Column(name = "last_name")
    String lastName;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    Address address;

    @JsonProperty("email")
    String email;

    @JsonProperty("phone")
    String phone;

    @JsonProperty("legacy_id")
    @Column(name = "legacy_id")
    Long legacyId;

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
        Customer customer = (Customer) o;
        return Objects.equals(firstName, customer.firstName)
                && Objects.equals(lastName, customer.lastName)
                && Objects.equals(phone, customer.phone)
                && Objects.equals(email, customer.email);
    }
}
