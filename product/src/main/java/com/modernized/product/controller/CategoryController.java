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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public Flux<CategoryDto> getAllCategories() {

        return categoryRepository.findAll()
                .map(it -> {
                    return modelMapper.map(it, CategoryDto.class);
                });
    }

    @GetMapping("/{id}")
    public Mono<CategoryDto> getCategoryById(@PathVariable Integer id) {

        return categoryRepository.findById(id)
                .map(it -> {
                    return modelMapper.map(it, CategoryDto.class);
                });
    }
}
