package com.legacy.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.payment.config.Actions;
import com.legacy.payment.event.PaymentEvent;
import com.legacy.payment.model.Customer;
import com.legacy.payment.model.Order;
import com.legacy.payment.model.Payment;
import com.legacy.payment.repository.CustomerRepository;
import com.legacy.payment.repository.OrderRepository;
import com.legacy.payment.repository.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired(required = false )
    private ModelMapper mapper;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(PaymentEvent event) {

        switch (event.getOp()) {
            case Actions.CREATE:
            case Actions.READ:
                insert(event);
                break;
        }
    }

    @Override
    public void insert(PaymentEvent event) {

        Payment payment = event.getAfter();
        if (Objects.equals(payment.getCreatedBy(), modCreatedBy)) return;

        Optional<Payment> optionalPayment = paymentRepository.findTopByLegacyId(payment.getId());
        if(optionalPayment.isPresent()) {
            logger.info("duplicate payment, no need to insert the order!");
            return;
        }
        payment.setLegacyId(payment.getId());
        payment.setLegacyOrderId(payment.getOrderId());
        payment.setCreatedBy(legacyCreatedBy);
        payment.setCreatedDate(event.getTimestamp());
        payment.setId(null);

        Optional<Customer> optionalCustomer = customerRepository.findTopByLegacyId(payment.getCustomerId());
        Optional<Order> optionalOrder = orderRepository.findTopByLegacyId(payment.getOrderId());

        if (optionalCustomer.isPresent() && optionalOrder.isPresent()) {
            payment.setCustomer(optionalCustomer.get());
            payment.setOrder(optionalOrder.get());
            paymentRepository.save(payment);
        }
    }

    @Override
    public void update(Long id, Long legacyId) {

        Optional<Payment> optionalOrder = paymentRepository.findById(id);
        optionalOrder.ifPresent(payment -> {
            if (payment.getLegacyId() == null) {
                payment.setLegacyId(legacyId);
                payment.setUpdatedBy(legacyCreatedBy);
                payment.setUpdatedDate(new Date());
                paymentRepository.save(payment);
            }
        });
    }
}
