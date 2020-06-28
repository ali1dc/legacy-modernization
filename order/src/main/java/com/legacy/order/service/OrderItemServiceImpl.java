package com.legacy.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.order.config.Actions;
import com.legacy.order.event.OrderItemEvent;
import com.legacy.order.model.Order;
import com.legacy.order.model.OrderItem;
import com.legacy.order.model.Product;
import com.legacy.order.repository.OrderItemRepository;
import com.legacy.order.repository.OrderRepository;
import com.legacy.order.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Value(value = "${created-by.legacy}")
    private String legacyCreatedBy;
    @Value(value = "${created-by.mod}")
    private String modCreatedBy;

    @Override
    public void eventHandler(String data) {


        JsonNode jsonNode;
        OrderItemEvent event;
        try {
            jsonNode = jsonMapper.readTree(data).at("/payload");
            event = jsonMapper.readValue(jsonNode.toString(), OrderItemEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        switch (event.getOp()) {
            case Actions.CREATE:
                insert(event);
                break;
            case Actions.UPDATE:
                update(event);
                break;
            case Actions.DELETE:
                delete(event);
                break;
        }    }

    @Override
    public void insert(OrderItemEvent event) {

        OrderItem orderItem = event.getAfter();
        if (Objects.equals(orderItem.getCreatedBy(), modCreatedBy)) return;
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findTopByLegacyId(orderItem.getId());
        if (optionalOrderItem.isPresent()) {
            logger.info("duplicate order item, no need to insert the item!");
            return;
        }

        orderItem.setLegacyId(orderItem.getId());
        orderItem.setLegacyOrderId(orderItem.getOrderId());
        orderItem.setLegacyProductId(orderItem.getProductId());
        orderItem.setCreatedBy(legacyCreatedBy);
        orderItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        orderItem.setId(null);
        // handle order-id product-id
        Optional<Product> optionalProduct = productRepository.findTopByLegacyId(orderItem.getProductId());
        Optional<Order> optionalOrder = orderRepository.findTopByLegacyId(orderItem.getOrderId());
        if (optionalOrder.isPresent() && optionalProduct.isPresent()) {
            orderItem.setOrder(optionalOrder.get());
            orderItem.setProduct(optionalProduct.get());
            orderItemRepository.save(orderItem);
        }
    }

    @Override
    public void update(OrderItemEvent event) {

        Optional<OrderItem> optionalOrderItem =
                orderItemRepository.findTopByLegacyId(event.getAfter().getId());
        if (!optionalOrderItem.isPresent()) return;

        OrderItem orderItem = optionalOrderItem.get();
        orderItem.setQuantity(event.getAfter().getQuantity());
        orderItem.setUnitPrice(event.getAfter().getUnitPrice());
        orderItem.setUpdatedBy(legacyCreatedBy);
        orderItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        orderItemRepository.save(orderItem);
    }

    @Override
    public void update(Long id, Long legacyId) {

        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(id);
        optionalOrderItem.ifPresent(item -> {
            if (item.getLegacyId() == null) {
                item.setLegacyId(legacyId);
                item.setUpdatedBy(legacyCreatedBy);
                item.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                orderItemRepository.save(item);
            }
        });
    }

    @Override
    public void delete(OrderItemEvent event) {

        logger.info("deleting records; not implemented yet!");
    }
}
