package com.legacy.order.service;

import com.legacy.order.event.CustomerEvent;

public interface CustomerService {

    void eventHandler(CustomerEvent event);

    void save(CustomerEvent event);
    void delete(CustomerEvent event);
}
