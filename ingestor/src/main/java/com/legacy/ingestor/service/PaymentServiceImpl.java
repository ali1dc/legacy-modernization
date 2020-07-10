package com.legacy.ingestor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.ingestor.config.KafkaTopics;
import com.legacy.ingestor.config.OrderStatuses;
import com.legacy.ingestor.dto.Payment;
import com.legacy.ingestor.events.PaymentEvent;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.model.LegacyPayment;
import com.legacy.ingestor.repository.OrderRepository;
import com.legacy.ingestor.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void save(PaymentEvent event) {

        logger.info("inserting payment record!");
        Payment payment = event.getAfter();

        Optional<LegacyOrder> optionalLegacyOrder = orderRepository.findById(payment.getLegacyOrderId());
        optionalLegacyOrder.ifPresent(order -> {
            LegacyPayment legacyPayment = LegacyPayment.builder()
                    .createdBy(modCreatedBy)
                    .amount(payment.getAmount())
                    .successful(payment.getSuccessful())
                    .order(order)
                    .build();
            paymentRepository.save(legacyPayment);
            kafkaTemplate.send(KafkaTopics.PAYMENT,
                    event.getAfter().getId().toString(),
                    legacyPayment.getId().toString());
            orderService.sendOrderStatus(payment.getOrderId(),
                    payment.getLegacyOrderId(),
                    OrderStatuses.CHARGED);
        });
    }
}
