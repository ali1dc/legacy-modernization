package com.legacy.ingestor.config;

public interface KafkaTopics {
    String ORDER_STATUS_TOPIC = "order-status-events";
    String CATEGORY = "category-legacy-ids";
    String PRODUCT = "product-legacy-ids";
    String CUSTOMER = "customer-legacy-ids";
    String ORDER = "order-legacy-ids";
    String ORDER_ITEM = "order-item-legacy-ids";
    String PAYMENT = "payment-legacy-ids";
    String SHIPPING = "shipping-legacy-ids";
}
