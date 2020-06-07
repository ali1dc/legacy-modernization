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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<ProductDto> getAllProducts() {

        List<ProductDto> products = new ArrayList<>();
        productRepository.findAll().forEach(product -> {
            products.add(modelMapper.map(product, ProductDto.class));
        });
        return products;
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {

        Optional<ProductDto> productDto = productRepository.findById(id)
                .map(product -> {
                    ProductDto dto = modelMapper.map(product, ProductDto.class);
                    List<String> categoryNames = new ArrayList<>();
                    productCategoryRepository.findByProductId(id).ifPresent(list -> {
                        list.forEach(cat -> categoryNames.add(cat.getCategory().getName()));
                    });
                    dto.setCategories(categoryNames);
                    return dto;
                });

        return productDto.orElse(new ProductDto());
    }
}
