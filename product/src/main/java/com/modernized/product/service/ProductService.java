package com.modernized.product.service;


import com.modernized.product.event.ProductEvent;

public interface ProductService {

    void productHandler(String data);
    void insert(ProductEvent event);
    void update(ProductEvent event);
    void delete(ProductEvent event);
}
