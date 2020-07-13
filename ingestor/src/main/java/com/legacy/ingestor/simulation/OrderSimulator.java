package com.legacy.ingestor.simulation;

import com.legacy.ingestor.config.OrderStatuses;
import com.legacy.ingestor.model.LegacyCustomer;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.model.LegacyOrderItem;
import com.legacy.ingestor.model.LegacyProduct;
import com.legacy.ingestor.repository.CustomerRepository;
import com.legacy.ingestor.repository.OrderItemRepository;
import com.legacy.ingestor.repository.OrderRepository;
import com.legacy.ingestor.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class OrderSimulator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String createdBy = "random-generator";
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Value("#{new Boolean('${simulator-enabled}')}")
    private Boolean simulatorEnabled;

    @Scheduled(fixedRate = 1000)
    public void insertOrder() {

        if(!simulatorEnabled) return;

        logger.info("order generator!");
        Optional<LegacyCustomer> optionalCustomer = customerRepository.findRandom();
        optionalCustomer.ifPresent(customer -> {
            logger.info("Random customer: {}, {}, {}", customer.getId(), customer.getFirstName(), customer.getLastName());
            // create order
            LegacyOrder order = LegacyOrder.builder()
                    .orderDate(new Timestamp(System.currentTimeMillis()))
                    .createdBy(createdBy)
                    .customer(customer)
                    .status(OrderStatuses.PENDING)
                    .build();
            orderRepository.save(order);
            // create some order items
            Random random = new Random();
            int maxOrderItems = 5;
            int orderItemCount = random.nextInt(maxOrderItems);
            List<LegacyOrderItem> items = new ArrayList<>();
            orderItemCount = orderItemCount == 0 ? 1 : orderItemCount;
            for (int i = 0; i < orderItemCount; i++) {
                // select random product and create order items
                Optional<LegacyProduct> optionalProduct = productRepository.findRandom();
                optionalProduct.ifPresent(product -> {
                    LegacyOrderItem orderItem = LegacyOrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(1)
                            .unitPrice(product.getListPrice())
                            .createdBy(createdBy)
                            .build();
                    items.add(orderItem);
                    orderItemRepository.saveAll(items);
                });
            }
        });
    }
}
