package com.legacy.ingestor.service;

import com.legacy.ingestor.events.CategoryEvent;
import com.legacy.ingestor.model.Category;

public interface CategoryService {

    Category insert(CategoryEvent event);
    Category update(CategoryEvent event);
    void delete(CategoryEvent event);
}
