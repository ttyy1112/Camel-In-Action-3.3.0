package com.atang.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SedaConcurrentConsumers extends CamelTestSupport {

    @Test
    public void testConcurrentConsumers() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            template.sendBody("seda:a", "a-" + (i + 1));
        }

        Thread.sleep(10000L);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // 一个consumer启动两个线程消费队列
                from("seda:a?multipleConsumers=true&concurrentConsumers=2").log("receive message 1 body ${body}");

                // 一个endpoint启用两个consumer，队列消息通过广播发送到各个消费者
                from("seda:a?multipleConsumers=true").log("receive message 2-1 body ${body}");
                from("seda:a?multipleConsumers=true").log("receive message 2-2 body ${body}");
            }
        };
    }
}
