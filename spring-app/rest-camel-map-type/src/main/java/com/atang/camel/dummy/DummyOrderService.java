package com.atang.camel.dummy;


import com.atang.camel.OrderService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component("orderService")
public class DummyOrderService implements OrderService {

    // in memory dummy order system
    private Map<Integer, Map> orders = new HashMap<>();

    private final AtomicInteger idGen = new AtomicInteger();

    public DummyOrderService() {
        // setup some dummy orders to start with
        setupDummyOrders();
    }

    @Override
    public Map getOrder(int orderId) {
        return orders.get(orderId);
    }

    @Override
    public void updateOrder(Map order) {
        int id = (int) order.get("id");
        orders.remove(id);
        orders.put(id, order);
    }

    @Override
    public String createOrder(Map order) {
        int id = idGen.incrementAndGet();
        order.put("id", id);
        orders.put(id, order);
        return "" + id;
    }

    @Override
    public void cancelOrder(int orderId) {
        orders.remove(orderId);
    }

    public void setupDummyOrders() {
        Map order = new LinkedHashMap();
        order.put("amount", 1);
        order.put("partName", "motor");
        order.put("customerName", "honda");
        createOrder(order);

        order = new LinkedHashMap();
        order.put("amount", 3);
        order.put("partName", "brake");
        order.put("customerName", "toyota");
        createOrder(order);
    }

}
