package com.legacy.payment.controller;

import com.legacy.payment.config.StateStores;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @GetMapping("/orders")
    public List<Float> orderTotals() {
        List<Float> totals = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, Float> store =
                interactiveQueryService.getQueryableStore(StateStores.ORDER_TOTAL, QueryableStoreTypes.keyValueStore());
        KeyValueIterator<Long, Float> all = store.all();
        while (all.hasNext()) {
            KeyValue<Long, Float> value = all.next();
            totals.add(value.value);
        }
        return totals;
    }

    @GetMapping("/orders/{id}")
    public Float orderTotal(@PathVariable Long id) {
        List<Float> totals = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, Float> store =
                interactiveQueryService.getQueryableStore(StateStores.ORDER_TOTAL, QueryableStoreTypes.keyValueStore());
        Float total = store.get(id);

        return total;
    }
}
