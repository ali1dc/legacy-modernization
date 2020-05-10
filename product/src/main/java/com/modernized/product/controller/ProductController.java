package com.modernized.product.controller;

import com.modernized.product.dto.ProductDto;
import com.modernized.product.repository.ProductCategoryRepository;
import com.modernized.product.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @GetMapping
    public Flux<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .map(it -> modelMapper.map(it, ProductDto.class));
    }

    @GetMapping("/{id}")
    public Mono<ProductDto> getProductById(@PathVariable Integer id) {
        Mono<List<String>> categoryNames = productCategoryRepository.findAllCategoryNameByProductId(id).collectList();
        Mono<ProductDto> productDto = productRepository.findById(id)
                .map(it -> {
                    return modelMapper.map(it, ProductDto.class);
                });
        return Mono.zip(productDto, categoryNames)
                .map(tuple -> {
                    tuple.getT1().setCategories(tuple.getT2());
                    return tuple.getT1();
                });
    }
}
