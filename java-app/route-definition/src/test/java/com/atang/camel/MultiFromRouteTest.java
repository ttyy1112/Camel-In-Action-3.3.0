package com.atang.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MultiFromRouteTest extends CamelTestSupport {

    @Test
    public void testMultiFrom() throws InterruptedException {
        template.sendBody("seda:a", "a");
        template.sendBody("seda:b", "b");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        /**
         * 一个路由多个input
         */
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:a").from("seda:b").log("receive message body ${body}");
            }
        };
    }

}
