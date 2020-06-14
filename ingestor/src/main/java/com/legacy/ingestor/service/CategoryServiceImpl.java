package com.legacy.ingestor.service;

import com.legacy.ingestor.config.LegacyIdTopics;
import com.legacy.ingestor.events.CategoryEvent;
import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CategoryRepository categoryRepository;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public Category insert(CategoryEvent event) {
        logger.info("inserting the record");
        // insert category if not exists in legacy db
        String key = event.getAfter().getId().toString();
        Category category = event.getAfter();
        Optional<Category> legacyCategory = categoryRepository.findTopByName(category.getName());

        if (legacyCategory.isPresent()) {
            logger.info("category record exists, we do not need to insert it again!");
            kafkaTemplate.send(LegacyIdTopics.CATEGORY, key, legacyCategory.get().getId().toString());
            return category;
        }

        category.setCreatedBy(modCreatedBy);
        categoryRepository.save(category);
        logger.info("category id: {} - name: {} was inserted to the legacy db.",
                category.getId(),
                category.getName());

        // we can set this with a transaction annotation.
        kafkaTemplate.send(LegacyIdTopics.CATEGORY, key, category.getId().toString());
        return category;
    }

    @Override
    public Category update(CategoryEvent event) {
        logger.info("updating the record");
        // find legacy side id
        // and handle duplicate updates
        Optional<Category> legacyCategoryOptional = categoryRepository.findTopByName(event.getBefore().getName());
        if (!legacyCategoryOptional.isPresent()) {
            legacyCategoryOptional = categoryRepository.findTopByName(event.getAfter().getName());
            return legacyCategoryOptional.get();
        }
        // set proper values
        Category legacyCategory = legacyCategoryOptional.get();
        legacyCategory.setName(event.getAfter().getName());
        legacyCategory.setCreatedBy(modCreatedBy);
        legacyCategory = categoryRepository.save(legacyCategory);
        return legacyCategory;
    }

    @Override
    public void delete(CategoryEvent event) {
        logger.info("deleting records; not implemented yet!");
    }
}
