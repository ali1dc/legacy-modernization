package com.modernized.product.service;

import com.modernized.product.event.CategoryEvent;

public interface CategoryService {

    void categoryHandler(CategoryEvent event);
    void insert(CategoryEvent event);
    void update(CategoryEvent event);
    void update(Long id, Long legacyId);
    void delete(CategoryEvent event);
}
