package com.modernized.product.controller;

import com.modernized.product.dto.ProductDto;
import com.modernized.product.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public Flux<ProductDto> getAllCategories() {

        return productRepository.findAll()
                .map(it -> {
                    return modelMapper.map(it, ProductDto.class);
                });
    }

    @GetMapping("/{id}")
    public Mono<ProductDto> getCategoryById(@PathVariable Long id) {

        return productRepository.findById(id)
                .map(it -> {
                    return modelMapper.map(it, ProductDto.class);
                });
    }
}
