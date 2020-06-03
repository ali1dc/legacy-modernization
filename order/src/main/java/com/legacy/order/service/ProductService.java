package com.legacy.order.service;

import com.legacy.order.event.ProductEvent;

public interface ProductService {

    void eventHandler(String data);

    void save(ProductEvent event);
    void delete(ProductEvent event);
}
