package com.atang.camel;

import java.util.Map;

public interface OrderService {

    Map getOrder(int orderId);

    void updateOrder(Map order);

    String createOrder(Map order);

    void cancelOrder(int orderId);

}
