package com.atang.camel.eip.split;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SplitAggrDemo {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // 主表
        List<Map<String, String>> orders = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final Map<String, String> orderMap = new LinkedHashMap<>();
            orderMap.put("order_code", "order_" + (i + 1));
            orders.add(orderMap);
        }

        // 子表
        List<Map<String, String>> order_details = new ArrayList<>();
        final Iterator<Map<String, String>> order_iterator = orders.iterator();
        while (order_iterator.hasNext()) {
            final Map<String, String> order = order_iterator.next();

            for (int j = 0; j < 2; j++) {
                final Map<String, String> detailMap = new LinkedHashMap<>();
                detailMap.put("order_code", order.get("order_code"));
                detailMap.put("detail_code", order.get("order_code") + "_" + (j + 1));
                order_details.add(detailMap);
            }
        }

        // 孙表
        List<Map<String, String>> detail_details = new ArrayList<>();
        final Iterator<Map<String, String>> iterator = order_details.iterator();
        while (iterator.hasNext()) {
            final Map<String, String> order_detail = iterator.next();
            for (int j = 0; j < 2; j++) {
                final Map<String, String> detail_detail = new LinkedHashMap<>();
                detail_detail.put("order_code", order_detail.get("order_code"));
                detail_detail.put("detail_code", order_detail.get("detail_code"));
                detail_detail.put("detail_detail_code", order_detail.get("detail_code") + "_" + (j + 1));
                detail_details.add(detail_detail);
            }
        }

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("quartz://report?cron=0/5+*+*+*+*+?")
                        .setBody(exchange -> orders)
                        .process(exchange -> {
                            exchange.setProperty("orders", exchange.getIn().getBody());
                            exchange.setProperty("order_count", exchange.getIn().getBody(List.class).size());
//                            final AtomicInteger splitCount = new AtomicInteger(0);
//                            exchange.setProperty("splitCount", splitCount);
                        })
                        .split(body())
                        .process(exchange -> {
                            log.info("查询订单明细");
                            final List<Map<String, String>> details = order_details.stream()
                                    .filter(detail -> exchange.getIn().getBody(Map.class).get("order_code").equals(detail.get("order_code")))
                                    .collect(Collectors.toList());

                            final Map order = exchange.getIn().getBody(Map.class);
                            order.put("details", details);

                            exchange.getMessage().setBody(details);
                        })
//                        .aggregate(constant("true"), (oldExchange, newExchange) -> {
//                            if (oldExchange == null) {
//                                return newExchange;
//                            }
//
//                            final List oldEx = oldExchange.getIn().getBody(List.class);
//                            final List newEx = newExchange.getIn().getBody(List.class);
//                            oldEx.addAll(newEx);
//                            oldExchange.getIn().setBody(oldEx);
//                            return oldExchange;
//                        })
//                        .completionSize(exchangeProperty("orderSize"))
//                        .process(exchange -> {
//                            final AtomicInteger splitCount = exchange.getProperty("splitCount", AtomicInteger.class);
//                            splitCount.getAndAdd(exchange.getIn().getBody(List.class).size());
//                        })
                        .process(exchange -> {
                            exchange.setProperty("detail_count", exchange.getIn().getBody(List.class).size());
                        })
                        .split(body())
                        .process(exchange -> {
                            log.info("查询明细的明细");
                            final List<Map<String, String>> dds = detail_details.stream()
                                    .filter(detail_detail -> exchange.getIn().getBody(Map.class).get("detail_code").equals(detail_detail.get("detail_code")))
                                    .collect(Collectors.toList());

                            final Map order = exchange.getIn().getBody(Map.class);
                            order.put("detail_details", dds);
                            exchange.getMessage().setBody(dds);
                        })
//                        .aggregate(constant("true"), (oldExchange, newExchange) -> {
//                            log.info("聚合");
//                            final AtomicInteger splitCount = newExchange.getProperty("splitCount", AtomicInteger.class);
//                            splitCount.getAndDecrement();
//                            return newExchange;
//                        })
//                        .completionPredicate(exchange -> exchange.getProperty("splitCount", AtomicInteger.class).get() == 0)
                        .aggregate(constant("true"), (oldExchange, newExchange) -> newExchange)
                        .completionSize(exchangeProperty("detail_count"))
                        .aggregate(constant("true"), (oldExchange, newExchange) -> newExchange)
                        .completionSize(exchangeProperty("order_count"))
                        .process(exchange -> {
                            log.info("结束");
                            final List res = exchange.getProperty("orders", List.class);
                            System.out.println(res);
                        });
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(100000);

        // stop the CamelContext
        context.stop();
    }

}
