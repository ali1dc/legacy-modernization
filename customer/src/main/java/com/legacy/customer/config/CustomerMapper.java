package com.legacy.customer.config;

import com.legacy.customer.dto.AddressDto;
import com.legacy.customer.dto.LegacyCustomer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerMapper {

    public List<AddressDto> eventToAddress(LegacyCustomer legacyCustomer) {

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(AddressDto.builder()
                .address1(legacyCustomer.getBillingAddress1())
                .address2(legacyCustomer.getBillingAddress2())
                .city(legacyCustomer.getBillingCity())
                .state(legacyCustomer.getBillingState())
                .zip(legacyCustomer.getBillingZip())
                .addressType(AddressTypes.BILLING)
                .isDefault(true)
                .build());
        addresses.add(AddressDto.builder()
                .address1(legacyCustomer.getShippingAddress1())
                .address2(legacyCustomer.getShippingAddress2())
                .city(legacyCustomer.getShippingCity())
                .state(legacyCustomer.getShippingState())
                .zip(legacyCustomer.getShippingZip())
                .addressType(AddressTypes.SHIPPING)
                .isDefault(false)
                .build());

        return addresses;
    }
}
