package com.modernized.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modernized.product.config.Actions;
import com.modernized.product.event.CategoryEvent;
import com.modernized.product.model.Category;
import com.modernized.product.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ObjectMapper jsonMapper;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void categoryHandler(String data) {

        try {
            JsonNode jsonNode = jsonMapper.readTree(data).at("/payload");
            CategoryEvent event = jsonMapper.readValue(jsonNode.toString(), CategoryEvent.class);
            switch (event.getOp()) {
                case Actions.CREATE:
                case Actions.READ:
                    insert(event);
                    break;
                case Actions.UPDATE:
                    update(event);
                    break;
                case Actions.DELETE:
                    delete(event);
                    break;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(CategoryEvent event) {

        Category category = event.getAfter();
        // check if message is coming from mod
        if (Objects.equals(category.getCreatedBy(), modCreatedBy)) {
            logger.info("The message is coming from mod; nothing to do!");
            return;
        }

        Optional<Category> categoryExists = categoryRepository.findTopByName(category.getName());
        if(!categoryExists.isPresent()) {
            category.setLegacyId(category.getId());
            category.setId(null);
            category.setCreatedBy(legacyCreatedBy);
            category.setCreatedDate(event.getTimestamp());
            categoryRepository.save(category);
            logger.info("record saved: {}", category);        }
    }

    @Override
    public void update(CategoryEvent event) {

        // find mod side id
        Optional<Category> existingCategory = categoryRepository.findTopByName(event.getBefore().getName());

        existingCategory.ifPresent(category -> {
            if (!category.equals(event.getAfter())) {
                category.setName(event.getAfter().getName());
                category.setUpdatedBy(legacyCreatedBy);
                category.setUpdatedDate(event.getTimestamp());
                categoryRepository.save(category);
            }
        });
    }

    @Override
    public void delete(CategoryEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
