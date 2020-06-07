package com.modernized.product.controller;

import com.modernized.product.dto.CategoryDto;
import com.modernized.product.model.Category;
import com.modernized.product.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<CategoryDto> getAllCategories() {

        List<CategoryDto> categories = new ArrayList<>();
        categoryRepository.findAll().forEach(cat -> {
            categories.add(modelMapper.map(cat, CategoryDto.class));
        });
        return categories;
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {

        Optional<Category> optionalCategory = categoryRepository.findById(id);
        CategoryDto dto = new CategoryDto();
        if (optionalCategory.isPresent())
            dto = modelMapper.map(optionalCategory.get(), CategoryDto.class);

        return dto;
    }
}
