package com.legacy.ingestor.service;

import com.legacy.ingestor.config.LegacyIdTopics;
import com.legacy.ingestor.config.StateStores;
import com.legacy.ingestor.dto.Customer;
import com.legacy.ingestor.dto.Order;
import com.legacy.ingestor.dto.Payment;
import com.legacy.ingestor.events.PaymentEvent;
import com.legacy.ingestor.model.LegacyCustomer;
import com.legacy.ingestor.model.LegacyOrder;
import com.legacy.ingestor.model.LegacyPayment;
import com.legacy.ingestor.repository.CustomerRepository;
import com.legacy.ingestor.repository.OrderRepository;
import com.legacy.ingestor.repository.PaymentRepository;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private InteractiveQueryService interactiveQueryService;
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
                    .customer(order.getCustomer())
                    .build();
            paymentRepository.save(legacyPayment);
            kafkaTemplate.send(LegacyIdTopics.PAYMENT,
                    event.getAfter().getId().toString(),
                    legacyPayment.getId().toString());
        });
    }
}
