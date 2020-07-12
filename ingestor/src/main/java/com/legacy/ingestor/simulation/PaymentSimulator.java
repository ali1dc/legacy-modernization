package com.legacy.ingestor.simulation;

import com.legacy.ingestor.config.OrderStatuses;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.model.LegacyOrderItem;
import com.legacy.ingestor.model.LegacyPayment;
import com.legacy.ingestor.repository.CustomerRepository;
import com.legacy.ingestor.repository.OrderItemRepository;
import com.legacy.ingestor.repository.OrderRepository;
import com.legacy.ingestor.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentSimulator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String createdBy = "random-generator";
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 2000)
    public void makePayment() {

        // get random order
        Optional<LegacyOrder> optionalOrder = orderRepository.findRandomPending();
        optionalOrder.ifPresent(order -> {
            Optional<List<LegacyOrderItem>> optionalItems = orderItemRepository.findAllByOrderId(order.getId());
            optionalItems.ifPresent(items -> {
                // calculate total
                Float total = 0F;
                for (LegacyOrderItem item: items) {
                    total += item.getUnitPrice();
                }
                order.setStatus(OrderStatuses.CHARGED);
                orderRepository.save(order);
                LegacyPayment payment = LegacyPayment.builder()
                        .order(order)
                        .amount(total)
                        .successful(true)
                        .createdBy(createdBy)
                        .build();
                paymentRepository.save(payment);
            });
        });
    }
}
